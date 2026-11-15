package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.net.URI;
import java.util.Locale;

final class Setup {
  private Setup() {}

  static String baseUrl() {
    return baseUrl(false);
  }

  static String loopbackBaseUrl() {
    return baseUrl(true);
  }

  private static String baseUrl(boolean requireLoopback) {
    String environment = environment();
    String baseUrl = System.getProperty(Constants.BASE_URL_PROPERTY);

    if (baseUrl == null || baseUrl.isBlank()) {
      if (!environment.equals(Constants.DEFAULT_ENVIRONMENT)) {
        throw new IllegalStateException(Constants.ERROR_BASE_URL_REQUIRED.formatted(environment));
      }
      baseUrl = Constants.DEFAULT_BASE_URL;
    }

    URI uri = URI.create(baseUrl);
    String scheme = uri.getScheme();
    String host = uri.getHost();
    if (scheme == null
        || !(scheme.equalsIgnoreCase(Constants.HTTP_SCHEME)
            || scheme.equalsIgnoreCase(Constants.HTTPS_SCHEME))
        || host == null
        || uri.getUserInfo() != null
        || uri.getQuery() != null
        || uri.getFragment() != null) {
      throw new IllegalArgumentException(Constants.ERROR_INVALID_BASE_URL);
    }

    if (requireLoopback
        && (!environment.equals(Constants.DEFAULT_ENVIRONMENT) || !isLoopback(host))) {
      throw new IllegalArgumentException(Constants.ERROR_LOAD_LOOPBACK_REQUIRED);
    }

    return baseUrl;
  }

  static HttpProtocolBuilder httpProtocol(String baseUrl) {
    return http.baseUrl(baseUrl);
  }

  static ScenarioBuilder smokeScenario(String name) {
    return requestScenario(name, Constants.SCENARIO_SMOKE_GET);
  }

  static ScenarioBuilder requestScenario(String name) {
    return requestScenario(name, Constants.GENERIC_REQUEST_NAME);
  }

  private static ScenarioBuilder requestScenario(String name, String requestName) {
    String smokePath = System.getProperty(Constants.SMOKE_PATH_PROPERTY);
    if (smokePath == null || smokePath.isBlank()) {
      if (environment().equals(Constants.DEFAULT_ENVIRONMENT)) {
        smokePath = Constants.DEFAULT_SMOKE_PATH;
      } else {
        throw new IllegalStateException(Constants.ERROR_SMOKE_PATH_REQUIRED);
      }
    }
    if (!smokePath.startsWith(Constants.PATH_SEPARATOR)
        || smokePath.startsWith(Constants.PROTOCOL_RELATIVE_PREFIX)) {
      throw new IllegalArgumentException(Constants.ERROR_INVALID_SMOKE_PATH);
    }

    int expectedStatus =
        Integer.parseInt(
            System.getProperty(
                Constants.EXPECTED_STATUS_PROPERTY, Constants.DEFAULT_EXPECTED_STATUS));
    if (expectedStatus < Constants.FIRST_HTTP_STATUS
        || expectedStatus > Constants.LAST_HTTP_STATUS) {
      throw new IllegalArgumentException(Constants.ERROR_INVALID_EXPECTED_STATUS);
    }

    return scenario(name).exec(http(requestName).get(smokePath).check(status().is(expectedStatus)));
  }

  static Assertion[] performanceAssertions() {
    return new Assertion[] {
      global().responseTime().percentile(95.0).lt(500), global().failedRequests().percent().lt(1.0)
    };
  }

  static Assertion[] smokeAssertions() {
    return new Assertion[] {global().failedRequests().percent().lt(1.0)};
  }

  private static String environment() {
    String environment =
        System.getProperty(Constants.ENVIRONMENT_PROPERTY, Constants.DEFAULT_ENVIRONMENT);
    if (!environment.equals(Constants.DEFAULT_ENVIRONMENT)
        && !environment.equals(Constants.PREPROD_ENVIRONMENT)
        && !environment.equals(Constants.PROD_ENVIRONMENT)) {
      throw new IllegalArgumentException(Constants.ERROR_INVALID_ENVIRONMENT);
    }
    return environment;
  }

  private static boolean isLoopback(String host) {
    String normalizedHost = host.toLowerCase(Locale.ROOT);
    return normalizedHost.equals(Constants.LOOPBACK_LOCALHOST)
        || normalizedHost.equals(Constants.LOOPBACK_IPV4)
        || normalizedHost.equals(Constants.LOOPBACK_IPV6)
        || normalizedHost.equals(Constants.LOOPBACK_IPV6_BRACKETED);
  }
}
