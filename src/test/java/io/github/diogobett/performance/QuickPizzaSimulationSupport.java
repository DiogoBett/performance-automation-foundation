package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.net.URI;
import java.util.Locale;

final class QuickPizzaSimulationSupport {
  private static final String DEFAULT_BASE_URL = "http://localhost:3333";

  private QuickPizzaSimulationSupport() {}

  static String baseUrl() {
    String baseUrl = System.getProperty("baseUrl", DEFAULT_BASE_URL);
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

    if (!isLoopback(host)) {
      throw new IllegalArgumentException(
          "Performance simulations are restricted to localhost, 127.0.0.1, or ::1.");
    }

    return baseUrl;
  }

  static HttpProtocolBuilder httpProtocol(String baseUrl) {
    return http.baseUrl(baseUrl).acceptHeader("application/json");
  }

  static ScenarioBuilder pizzaRecommendationScenario(String name) {
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

  static Assertion[] performanceAssertions() {
    return new Assertion[] {
      global().responseTime().percentile(95.0).lt(500),
      global().failedRequests().percent().lt(1.0)
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
