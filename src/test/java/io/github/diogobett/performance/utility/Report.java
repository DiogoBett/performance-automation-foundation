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
      String allureResult =
          Constants.REPORT_ALLURE_RESULT_TEMPLATE.formatted(
              uuid,
              result.name(),
              result.simulation(),
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
    List<String> scenarios = new ArrayList<>();

    for (SimulationResult result : results) {
      long durationNanos = Math.multiplyExact(result.stop() - result.start(), 1_000_000L);
      String failureMessage =
          result.status().equals(Constants.REPORT_STATUS_FAILED)
              ? Constants.REPORT_ERROR_MESSAGE_FIELD_FORMAT.formatted(
                  Constants.REPORT_ERROR_MESSAGE_KEY,
                  Constants.REPORT_CUCUMBER_FAILURE_MESSAGE_FORMAT.formatted(
                      result.name(), Constants.REPORT_FAILURE_MESSAGE))
              : Constants.REPORT_EMPTY_STRING;
      scenarios.add(
          Constants.REPORT_CUCUMBER_SCENARIO_TEMPLATE.formatted(
              Constants.REPORT_SCENARIO_TYPE,
              result.name(),
              Constants.REPORT_FEATURE_ID
                  + Constants.REPORT_FEATURE_ID_SEPARATOR
                  + result.name().toLowerCase(Locale.ROOT),
              Instant.ofEpochMilli(result.start()),
              Constants.REPORT_STEP_NAME_FORMAT.formatted(result.name()),
              result.status(),
              durationNanos,
              failureMessage));
    }

    Files.createDirectories(reportFile.toAbsolutePath().getParent());
    Files.writeString(
        reportFile,
        Constants.REPORT_CUCUMBER_FEATURE_TEMPLATE.formatted(
            Constants.REPORT_FEATURE_NAME,
            Constants.REPORT_FEATURE_URI,
            Constants.REPORT_FEATURE_ID,
            String.join(Constants.REPORT_CUCUMBER_SCENARIO_SEPARATOR, scenarios)),
        StandardCharsets.UTF_8);
  }

  private record SimulationResult(
      String simulation, String name, String status, long start, long stop) {}
}
