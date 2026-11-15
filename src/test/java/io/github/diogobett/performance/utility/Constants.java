package io.github.diogobett.performance.utility;

import java.util.List;

public final class Constants {
  public static final String DEFAULT_ENVIRONMENT = "dev";
  public static final String PREPROD_ENVIRONMENT = "preprod";
  public static final String PROD_ENVIRONMENT = "prod";

  public static final String DEFAULT_BASE_URL = "https://quickpizza.grafana.com";
  public static final String QUICKPIZZA_HOST = "quickpizza.grafana.com";
  public static final String DEFAULT_SMOKE_PATH = "/ready";
  public static final String DEFAULT_EXPECTED_STATUS = "200";
  public static final int FIRST_HTTP_STATUS = 100;
  public static final int LAST_HTTP_STATUS = 599;
  public static final int PERFORMANCE_VIRTUAL_USERS = 5;
  public static final int LOAD_RAMP_DURATION_SECONDS = 5;
  public static final int LOAD_REQUESTS_PER_USER = 15;
  public static final int SPIKE_REQUESTS_PER_USER = 1;
  public static final int REQUEST_PAUSE_SECONDS = 1;

  public static final String ENVIRONMENT_PROPERTY = "environment";
  public static final String BASE_URL_PROPERTY = "baseUrl";
  public static final String SMOKE_PATH_PROPERTY = "smokePath";
  public static final String EXPECTED_STATUS_PROPERTY = "expectedStatus";

  public static final String HTTP_SCHEME = "http";
  public static final String HTTPS_SCHEME = "https";
  public static final String LOOPBACK_LOCALHOST = "localhost";
  public static final String LOOPBACK_IPV4 = "127.0.0.1";
  public static final String LOOPBACK_IPV6 = "::1";
  public static final String LOOPBACK_IPV6_BRACKETED = "[::1]";
  public static final String PATH_SEPARATOR = "/";
  public static final String PROTOCOL_RELATIVE_PREFIX = "//";

  public static final String SCENARIO_SMOKE_GET = "Smoke GET";
  public static final String SCENARIO_NAME_SMOKE = "Smoke";
  public static final String SCENARIO_NAME_LOAD = "Load";
  public static final String SCENARIO_NAME_SPIKE = "Spike";
  public static final String GENERIC_REQUEST_NAME = "Generic GET";

  public static final String REPORT_INPUT_USAGE =
      "Usage: Report <simulation-results.tsv> <allure-results-directory> <generated-report-file>";
  public static final String REPORT_STATUS_PASSED = "passed";
  public static final String REPORT_STATUS_FAILED = "failed";
  public static final String REPORT_RESULT_FILE_SUFFIX = "-result.json";
  public static final String REPORT_EMPTY_STRING = "";
  public static final String REPORT_CUCUMBER_FEATURE_SEPARATOR = ",\n";
  public static final List<String> REPORT_SIMULATIONS =
      List.of("GenericSmokeSimulation", "GenericLoadSimulation", "GenericSpikeSimulation");
  public static final List<String> REPORT_SCENARIO_NAMES =
      List.of(SCENARIO_NAME_SMOKE, SCENARIO_NAME_LOAD, SCENARIO_NAME_SPIKE);
  public static final String REPORT_FEATURE_URI_SUFFIX = ".feature";
  public static final String REPORT_SCENARIO_TYPE = "scenario";
  public static final String REPORT_STEP_NAME_FORMAT = "Gatling %s simulation";
  public static final String REPORT_FAILURE_MESSAGE =
      "Gatling simulation failed. See the Gatling HTML report and CI logs.";
  public static final String REPORT_CUCUMBER_FAILURE_MESSAGE_FORMAT = "%s: %s";
  public static final String REPORT_EXPECTED_FIELDS_ERROR =
      "Expected four tab-separated fields: %s";
  public static final String REPORT_UNSUPPORTED_SIMULATION_ERROR =
      "Unsupported Gatling simulation: %s";
  public static final String REPORT_DUPLICATE_SIMULATION_ERROR =
      "Duplicate Gatling simulation result: %s";
  public static final String REPORT_UNSUPPORTED_STATUS_ERROR = "Unsupported scenario status: %s";
  public static final String REPORT_INVALID_TIMESTAMPS_ERROR = "Invalid timestamps for %s";
  public static final String REPORT_MISSING_SIMULATIONS_ERROR = "Missing simulation results: %s";
  public static final String REPORT_ERROR_MESSAGE_FIELD_FORMAT = ",\n      \"%s\": \"%s\"";
  public static final String REPORT_ALLURE_RESULT_TEMPLATE =
      """
      {
        "uuid": "%s",
        "name": "%s",
        "fullName": "io.github.diogobett.performance.simulations.%s",
        "status": "%s",
        "stage": "finished",
        "start": %d,
        "stop": %d,
        "labels": [
          {"name": "suite", "value": "Performance Scenarios"},
          {"name": "framework", "value": "Gatling"},
          {"name": "language", "value": "Java"}
        ]%s
      }
      """;
  public static final String REPORT_ALLURE_FAILURE_DETAILS_TEMPLATE =
      """
      ,
      "statusDetails": {
        "known": false,
        "muted": false,
        "flaky": false,
        "message": "%s",
        "trace": ""
      }
      """;
  public static final String REPORT_CUCUMBER_FEATURE_TEMPLATE =
      """
      {
        "name": "%s",
        "uri": "%s",
        "id": "%s",
        "elements": [
      %s
        ]
      }
      """;
  public static final String REPORT_CUCUMBER_REPORT_TEMPLATE =
      """
      [
      %s
      ]
      """;
  public static final String REPORT_CUCUMBER_SCENARIO_TEMPLATE =
      """
      {
        "type": "%s",
        "name": "%s",
        "id": "%s",
        "start_timestamp": "%s",
        "steps": [
          {
            "name": "%s",
            "result": {
              "status": "%s",
              "duration": %d%s
            }
          }
        ]
      }
      """;
  public static final String REPORT_FEATURE_ID_SEPARATOR = ";";
  public static final String REPORT_ERROR_MESSAGE_KEY = "error_message";
  public static final String REPORT_TSV_FIELD_SEPARATOR = "\t";

  public static final String ERROR_INVALID_ENVIRONMENT =
      "environment must be dev, preprod, or prod.";
  public static final String ERROR_BASE_URL_REQUIRED =
      "Set -D" + BASE_URL_PROPERTY + " for the %s environment.";
  public static final String ERROR_INVALID_BASE_URL =
      BASE_URL_PROPERTY
          + " must be an absolute HTTP(S) URL without credentials, query, or fragment.";
  public static final String ERROR_PERFORMANCE_TARGET_REQUIRED =
      "Load and spike simulations are restricted to DEV QuickPizza or a loopback target.";
  public static final String ERROR_SMOKE_PATH_REQUIRED =
      "Set -D" + SMOKE_PATH_PROPERTY + " for the selected environment.";
  public static final String ERROR_INVALID_SMOKE_PATH =
      SMOKE_PATH_PROPERTY + " must be an absolute path on the configured host.";
  public static final String ERROR_INVALID_EXPECTED_STATUS =
      EXPECTED_STATUS_PROPERTY
          + " must be between "
          + FIRST_HTTP_STATUS
          + " and "
          + LAST_HTTP_STATUS
          + ".";

  private Constants() {}
}
