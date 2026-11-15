package io.github.diogobett.performance.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class Report {
  private Report() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      throw new IllegalArgumentException(Constants.REPORT_INPUT_USAGE);
    }

    List<SimulationResult> results = readResults(Path.of(args[0]));
    writeAllureResults(results, Path.of(args[1]));
    writeCucumberReport(results, Path.of(args[2]));
  }

  private static List<SimulationResult> readResults(Path resultsFile) throws IOException {
    List<SimulationResult> results = new ArrayList<>();
    Set<String> seen = new HashSet<>();

    for (String line : Files.readAllLines(resultsFile, StandardCharsets.UTF_8)) {
      String[] fields = line.split(Constants.REPORT_TSV_FIELD_SEPARATOR, -1);
      if (fields.length != 4) {
        throw new IllegalArgumentException(Constants.REPORT_EXPECTED_FIELDS_ERROR.formatted(line));
      }

      String simulation = fields[0];
      int scenarioIndex = Constants.REPORT_SIMULATIONS.indexOf(simulation);
      if (scenarioIndex < 0) {
        throw new IllegalArgumentException(
            Constants.REPORT_UNSUPPORTED_SIMULATION_ERROR.formatted(simulation));
      }
      if (!seen.add(simulation)) {
        throw new IllegalArgumentException(
            Constants.REPORT_DUPLICATE_SIMULATION_ERROR.formatted(simulation));
      }

      String status = fields[1];
      if (!status.equals(Constants.REPORT_STATUS_PASSED)
          && !status.equals(Constants.REPORT_STATUS_FAILED)) {
        throw new IllegalArgumentException(
            Constants.REPORT_UNSUPPORTED_STATUS_ERROR.formatted(status));
      }

      long start = Long.parseLong(fields[2]);
      long stop = Long.parseLong(fields[3]);
      if (start < 0 || stop < start) {
        throw new IllegalArgumentException(
            Constants.REPORT_INVALID_TIMESTAMPS_ERROR.formatted(simulation));
      }

      results.add(
          new SimulationResult(
              simulation, Constants.REPORT_SCENARIO_NAMES.get(scenarioIndex), status, start, stop));
    }

    if (seen.size() != Constants.REPORT_SIMULATIONS.size()) {
      Set<String> missing = new HashSet<>(Constants.REPORT_SIMULATIONS);
      missing.removeAll(seen);
      throw new IllegalArgumentException(
          Constants.REPORT_MISSING_SIMULATIONS_ERROR.formatted(missing));
    }

    return results;
  }

  private static void writeAllureResults(List<SimulationResult> results, Path outputDirectory)
      throws IOException {
    Files.createDirectories(outputDirectory);

    for (SimulationResult result : results) {
      String uuid = UUID.randomUUID().toString();
      String failureDetails =
          result.status().equals(Constants.REPORT_STATUS_FAILED)
              ? Constants.REPORT_ALLURE_FAILURE_DETAILS_TEMPLATE.formatted(
                  Constants.REPORT_FAILURE_MESSAGE)
              : Constants.REPORT_EMPTY_STRING;
      String stepName = reportStepName(result);
      String allureResult =
          Constants.REPORT_ALLURE_RESULT_TEMPLATE.formatted(
              uuid,
              result.name(),
              result.simulation(),
              result.status(),
              result.start(),
              result.stop(),
              result.name(),
              stepName,
              result.status(),
              result.start(),
              result.stop(),
              failureDetails);

      Files.writeString(
          outputDirectory.resolve(uuid + Constants.REPORT_RESULT_FILE_SUFFIX),
          allureResult.strip() + System.lineSeparator(),
          StandardCharsets.UTF_8);
    }
  }

  private static void writeCucumberReport(List<SimulationResult> results, Path reportFile)
      throws IOException {
    List<String> features = new ArrayList<>();

    for (SimulationResult result : results) {
      long durationNanos = Math.multiplyExact(result.stop() - result.start(), 1_000_000L);
      String featureId = result.name().toLowerCase(Locale.ROOT);
      String failureMessage =
          result.status().equals(Constants.REPORT_STATUS_FAILED)
              ? Constants.REPORT_ERROR_MESSAGE_FIELD_FORMAT.formatted(
                  Constants.REPORT_ERROR_MESSAGE_KEY,
                  Constants.REPORT_CUCUMBER_FAILURE_MESSAGE_FORMAT.formatted(
                      result.name(), Constants.REPORT_FAILURE_MESSAGE))
              : Constants.REPORT_EMPTY_STRING;
      String scenario =
          Constants.REPORT_CUCUMBER_SCENARIO_TEMPLATE.formatted(
              Constants.REPORT_SCENARIO_TYPE,
              result.name(),
              featureId + Constants.REPORT_FEATURE_ID_SEPARATOR + featureId,
              Instant.ofEpochMilli(result.start()),
              reportStepName(result),
              result.status(),
              durationNanos,
              failureMessage);
      features.add(
          Constants.REPORT_CUCUMBER_FEATURE_TEMPLATE.formatted(
              result.name(), featureId + Constants.REPORT_FEATURE_URI_SUFFIX, featureId, scenario));
    }

    Files.createDirectories(reportFile.toAbsolutePath().getParent());
    Files.writeString(
        reportFile,
        Constants.REPORT_CUCUMBER_REPORT_TEMPLATE.formatted(
            String.join(Constants.REPORT_CUCUMBER_FEATURE_SEPARATOR, features)),
        StandardCharsets.UTF_8);
  }

  private static String reportStepName(SimulationResult result) {
    String baseUrl = configuredValue(Constants.BASE_URL_PROPERTY, Constants.REPORT_BASE_URL_ENV);
    String smokePath =
        configuredValue(Constants.SMOKE_PATH_PROPERTY, Constants.REPORT_SMOKE_PATH_ENV);
    if (baseUrl == null || baseUrl.isBlank()) {
      baseUrl = Constants.DEFAULT_BASE_URL;
    }
    if (smokePath == null || smokePath.isBlank()) {
      smokePath = Constants.DEFAULT_SMOKE_PATH;
    }
    String target =
        baseUrl.endsWith(Constants.PATH_SEPARATOR)
            ? baseUrl.substring(0, baseUrl.length() - 1) + smokePath
            : baseUrl + smokePath;

    return switch (result.name()) {
      case Constants.SCENARIO_NAME_SMOKE -> Constants.REPORT_SMOKE_STEP_FORMAT.formatted(target);
      case Constants.SCENARIO_NAME_LOAD ->
          Constants.REPORT_LOAD_STEP_FORMAT.formatted(
              Constants.PERFORMANCE_VIRTUAL_USERS,
              Constants.LOAD_RAMP_DURATION_SECONDS,
              target,
              Constants.LOAD_REQUESTS_PER_USER);
      case Constants.SCENARIO_NAME_SPIKE ->
          Constants.REPORT_SPIKE_STEP_FORMAT.formatted(
              Constants.PERFORMANCE_VIRTUAL_USERS, target, Constants.SPIKE_REQUESTS_PER_USER);
      default ->
          throw new IllegalArgumentException(
              Constants.REPORT_UNSUPPORTED_SCENARIO_ERROR.formatted(result.name()));
    };
  }

  private static String configuredValue(String property, String environmentVariable) {
    String value = System.getProperty(property);
    return value == null || value.isBlank() ? System.getenv(environmentVariable) : value;
  }

  private record SimulationResult(
      String simulation, String name, String status, long start, long stop) {}
}
