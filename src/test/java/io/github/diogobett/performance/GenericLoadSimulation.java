package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;

public class GenericLoadSimulation extends Simulation {
  {
    String baseUrl = Setup.loopbackBaseUrl();

    setUp(
            Setup.requestScenario(Constants.SCENARIO_NAME_LOAD)
                .injectOpen(rampUsers(50).during(Duration.ofMinutes(2))))
        .protocols(Setup.httpProtocol(baseUrl))
        .assertions(Setup.performanceAssertions());
  }
}
