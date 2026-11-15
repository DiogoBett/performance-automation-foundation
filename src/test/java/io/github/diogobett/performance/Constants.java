package io.github.diogobett.performance;

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
