package io.github.diogobett.performance;

import java.util.List;

final class Constants {
  static final String DEFAULT_ENVIRONMENT = "dev";
  static final String PREPROD_ENVIRONMENT = "preprod";
  static final String PROD_ENVIRONMENT = "prod";

  static final String DEFAULT_BASE_URL = "https://quickpizza.grafana.com";
  static final String QUICKPIZZA_HOST = "quickpizza.grafana.com";
  static final String DEFAULT_SMOKE_PATH = "/ready";
  static final String DEFAULT_EXPECTED_STATUS = "200";
  static final int FIRST_HTTP_STATUS = 100;
  static final int LAST_HTTP_STATUS = 599;
  static final int PERFORMANCE_VIRTUAL_USERS = 5;
  static final int LOAD_RAMP_DURATION_SECONDS = 5;
  static final int LOAD_REQUESTS_PER_USER = 15;
  static final int SPIKE_REQUESTS_PER_USER = 1;
  static final int REQUEST_PAUSE_SECONDS = 1;

  static final String ENVIRONMENT_PROPERTY = "environment";
  static final String BASE_URL_PROPERTY = "baseUrl";
  static final String SMOKE_PATH_PROPERTY = "smokePath";
  static final String EXPECTED_STATUS_PROPERTY = "expectedStatus";

  static final String HTTP_SCHEME = "http";
  static final String HTTPS_SCHEME = "https";
  static final String LOOPBACK_LOCALHOST = "localhost";
  static final String LOOPBACK_IPV4 = "127.0.0.1";
  static final String LOOPBACK_IPV6 = "::1";
  static final String LOOPBACK_IPV6_BRACKETED = "[::1]";
  static final String PATH_SEPARATOR = "/";
  static final String PROTOCOL_RELATIVE_PREFIX = "//";

  static final String SCENARIO_SMOKE_GET = "Smoke GET";
  static final String SCENARIO_NAME_SMOKE = "Smoke";
  static final String SCENARIO_NAME_LOAD = "Load";
  static final String SCENARIO_NAME_SPIKE = "Spike";
  static final String GENERIC_REQUEST_NAME = "Generic GET";

  static final String REPORT_INPUT_USAGE =
      "Usage: Report <simulation-results.tsv> <allure-results-directory> <cucumber-report-file>";
  static final String REPORT_STATUS_PASSED = "passed";
  static final String REPORT_STATUS_FAILED = "failed";
  static final String REPORT_RESULT_FILE_SUFFIX = "-result.json";
  static final String REPORT_EMPTY_STRING = "";
  static final String REPORT_CUCUMBER_SCENARIO_SEPARATOR = ",\n";
  static final List<String> REPORT_SIMULATIONS =
      List.of("GenericSmokeSimulation", "GenericLoadSimulation", "GenericSpikeSimulation");
  static final List<String> REPORT_SCENARIO_NAMES =
      List.of(SCENARIO_NAME_SMOKE, SCENARIO_NAME_LOAD, SCENARIO_NAME_SPIKE);
  static final String REPORT_FEATURE_NAME = "Performance Scenarios";
  static final String REPORT_FEATURE_URI = "performance-scenarios.feature";
  static final String REPORT_FEATURE_ID = "performance-scenarios";
  static final String REPORT_SCENARIO_TYPE = "scenario";
  static final String REPORT_STEP_NAME_FORMAT = "Gatling %s simulation";
  static final String REPORT_FAILURE_MESSAGE =
      "Gatling simulation failed. See the Gatling HTML report and CI logs.";
  static final String REPORT_CUCUMBER_FAILURE_MESSAGE_FORMAT = "%s: %s";
  static final String REPORT_EXPECTED_FIELDS_ERROR = "Expected four tab-separated fields: %s";
  static final String REPORT_UNSUPPORTED_SIMULATION_ERROR = "Unsupported Gatling simulation: %s";
  static final String REPORT_DUPLICATE_SIMULATION_ERROR = "Duplicate Gatling simulation result: %s";
  static final String REPORT_UNSUPPORTED_STATUS_ERROR = "Unsupported scenario status: %s";
  static final String REPORT_INVALID_TIMESTAMPS_ERROR = "Invalid timestamps for %s";
  static final String REPORT_MISSING_SIMULATIONS_ERROR = "Missing simulation results: %s";
  static final String REPORT_ERROR_MESSAGE_FIELD_FORMAT = ",\n      \"%s\": \"%s\"";
  static final String REPORT_ALLURE_RESULT_TEMPLATE =
      """
      {
        "uuid": "%s",
        "name": "%s",
        "fullName": "io.github.diogobett.performance.%s",
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
  static final String REPORT_ALLURE_FAILURE_DETAILS_TEMPLATE =
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
  static final String REPORT_CUCUMBER_FEATURE_TEMPLATE =
      """
      [
        {
          "name": "%s",
          "uri": "%s",
          "id": "%s",
          "elements": [
      %s
          ]
        }
      ]
      """;
  static final String REPORT_CUCUMBER_SCENARIO_TEMPLATE =
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
  static final String REPORT_FEATURE_ID_SEPARATOR = ";";
  static final String REPORT_ERROR_MESSAGE_KEY = "error_message";
  static final String REPORT_TSV_FIELD_SEPARATOR = "\t";

  static final String ERROR_INVALID_ENVIRONMENT = "environment must be dev, preprod, or prod.";
  static final String ERROR_BASE_URL_REQUIRED =
      "Set -D" + BASE_URL_PROPERTY + " for the %s environment.";
  static final String ERROR_INVALID_BASE_URL =
      BASE_URL_PROPERTY
          + " must be an absolute HTTP(S) URL without credentials, query, or fragment.";
  static final String ERROR_PERFORMANCE_TARGET_REQUIRED =
      "Load and spike simulations are restricted to DEV QuickPizza or a loopback target.";
  static final String ERROR_SMOKE_PATH_REQUIRED =
      "Set -D" + SMOKE_PATH_PROPERTY + " for the selected environment.";
  static final String ERROR_INVALID_SMOKE_PATH =
      SMOKE_PATH_PROPERTY + " must be an absolute path on the configured host.";
  static final String ERROR_INVALID_EXPECTED_STATUS =
      EXPECTED_STATUS_PROPERTY
          + " must be between "
          + FIRST_HTTP_STATUS
          + " and "
          + LAST_HTTP_STATUS
          + ".";

  private Constants() {}
}
