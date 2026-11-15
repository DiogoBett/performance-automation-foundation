package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.net.URI;
import java.time.Duration;
import java.util.Locale;

final class Setup {
  private Setup() {}

  static String baseUrl() {
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

    return baseUrl;
  }

  static String performanceBaseUrl() {
    String baseUrl = baseUrl();
    URI uri = URI.create(baseUrl);
    String host = uri.getHost();

    if (!environment().equals(Constants.DEFAULT_ENVIRONMENT)
        || (!isLoopback(host) && !host.equalsIgnoreCase(Constants.QUICKPIZZA_HOST))) {
      throw new IllegalArgumentException(Constants.ERROR_PERFORMANCE_TARGET_REQUIRED);
    }

    return baseUrl;
  }

  static HttpProtocolBuilder httpProtocol(String baseUrl) {
    return http.baseUrl(baseUrl);
  }

  static ScenarioBuilder smokeScenario(String name) {
    return scenario(name)
        .exec(
            http(Constants.SCENARIO_SMOKE_GET)
                .get(smokePath())
                .check(status().is(expectedStatus())));
  }

  static ScenarioBuilder requestScenario(String name) {
    return repeatedRequestScenario(name, Constants.SPIKE_REQUESTS_PER_USER);
  }

  static ScenarioBuilder loadScenario(String name) {
    return repeatedRequestScenario(name, Constants.LOAD_REQUESTS_PER_USER);
  }

  private static ScenarioBuilder repeatedRequestScenario(String name, int requestCount) {
    String smokePath = smokePath();
    int expectedStatus = expectedStatus();

    return scenario(name)
        .repeat(requestCount)
        .on(
            exec(http(Constants.GENERIC_REQUEST_NAME)
                    .get(smokePath)
                    .check(status().is(expectedStatus)))
                .pause(Duration.ofSeconds(Constants.REQUEST_PAUSE_SECONDS)));
  }

  private static String smokePath() {
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

    return smokePath;
  }

  private static int expectedStatus() {
    int expectedStatus =
        Integer.parseInt(
            System.getProperty(
                Constants.EXPECTED_STATUS_PROPERTY, Constants.DEFAULT_EXPECTED_STATUS));
    if (expectedStatus < Constants.FIRST_HTTP_STATUS
        || expectedStatus > Constants.LAST_HTTP_STATUS) {
      throw new IllegalArgumentException(Constants.ERROR_INVALID_EXPECTED_STATUS);
    }

    return expectedStatus;
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
