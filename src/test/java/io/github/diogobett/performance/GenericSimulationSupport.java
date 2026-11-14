package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.net.URI;
import java.util.Locale;

final class GenericSimulationSupport {
  private static final String DEFAULT_BASE_URL = "http://localhost:3333";

  private GenericSimulationSupport() {}

  static String baseUrl(boolean allowRemoteTarget) {
    String environment = environment();

    String baseUrl = System.getProperty("baseUrl");
    if (baseUrl == null || baseUrl.isBlank()) {
      if (!environment.equals("dev")) {
        throw new IllegalStateException("Set -DbaseUrl for the " + environment + " environment.");
      }
      baseUrl = DEFAULT_BASE_URL;
    }

    URI uri = URI.create(baseUrl);
    String scheme = uri.getScheme();
    String host = uri.getHost();

    if (scheme == null
        || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
        || host == null
        || uri.getUserInfo() != null
        || uri.getQuery() != null
        || uri.getFragment() != null) {
      throw new IllegalArgumentException(
          "baseUrl must be an absolute HTTP(S) URL without credentials, query, or fragment.");
    }

    if (environment.equals("dev") && !isLoopback(host)) {
      throw new IllegalArgumentException("The dev environment must use a loopback URL.");
    }

    if (!allowRemoteTarget && (!environment.equals("dev") || !isLoopback(host))) {
      throw new IllegalArgumentException(
          "Load and spike simulations are restricted to a loopback dev target.");
    }

    return baseUrl;
  }

  static HttpProtocolBuilder httpProtocol(String baseUrl) {
    return http.baseUrl(baseUrl).acceptHeader("application/json");
  }

  static ScenarioBuilder scenarioForEnvironment(String name) {
    if (environment().equals("dev")) {
      return quickPizzaScenario(name);
    }

    String smokePath = requiredProperty("smokePath");
    if (!smokePath.startsWith("/") || smokePath.startsWith("//")) {
      throw new IllegalArgumentException(
          "smokePath must be an absolute path on the configured host.");
    }
    int expectedStatus = Integer.parseInt(System.getProperty("expectedStatus", "200"));
    if (expectedStatus < 100 || expectedStatus > 599) {
      throw new IllegalArgumentException("expectedStatus must be between 100 and 599.");
    }

    return scenario(name)
        .exec(http("Generic smoke GET").get(smokePath).check(status().is(expectedStatus)));
  }

  private static ScenarioBuilder quickPizzaScenario(String name) {
    String username = requiredCredential("QUICKPIZZA_USERNAME", "quickpizza.username");
    String password = requiredCredential("QUICKPIZZA_PASSWORD", "quickpizza.password");

    return scenario(name)
        .exec((Session session) -> session.set("username", username).set("password", password))
        .exec(
            http("Login")
                .post("/api/users/token/login")
                .header("Content-Type", "application/json")
                .body(StringBody("{\"username\":\"#{username}\",\"password\":\"#{password}\"}"))
                .check(status().is(200), jsonPath("$.token").saveAs("token")))
        .exec(
            http("Get pizza recommendation")
                .post("/api/pizza")
                .header("Authorization", "token #{token}")
                .header("Content-Type", "application/json")
                .body(StringBody("{}"))
                .check(status().is(200), jsonPath("$.pizza.id").exists()));
  }

  private static String environment() {
    String environment = System.getProperty("environment", "dev");
    if (!environment.equals("dev")
        && !environment.equals("preprod")
        && !environment.equals("prod")) {
      throw new IllegalArgumentException("environment must be dev, preprod, or prod.");
    }
    return environment;
  }

  private static String requiredProperty(String name) {
    String value = System.getProperty(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Set -D" + name + " for the selected environment.");
    }
    return value;
  }

  static Assertion[] performanceAssertions() {
    return new Assertion[] {
      global().responseTime().percentile(95.0).lt(500), global().failedRequests().percent().lt(1.0)
    };
  }

  private static String requiredCredential(String environmentVariable, String systemProperty) {
    String value = System.getenv(environmentVariable);
    if (value == null || value.isBlank()) {
      value = System.getProperty(systemProperty);
    }
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          "Set " + environmentVariable + " or -D" + systemProperty + " to run this simulation.");
    }
    return value;
  }

  private static boolean isLoopback(String host) {
    String normalizedHost = host.toLowerCase(Locale.ROOT);
    return normalizedHost.equals("localhost")
        || normalizedHost.equals("127.0.0.1")
        || normalizedHost.equals("::1")
        || normalizedHost.equals("[::1]");
  }
}
