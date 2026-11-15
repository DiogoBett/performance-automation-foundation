package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;

public class GenericLoadSimulation extends Simulation {
  {
    String baseUrl = Setup.performanceBaseUrl();

    setUp(
            Setup.loadScenario(Constants.SCENARIO_NAME_LOAD)
                .injectOpen(
                    rampUsers(Constants.PERFORMANCE_VIRTUAL_USERS)
                        .during(Duration.ofSeconds(Constants.LOAD_RAMP_DURATION_SECONDS))))
        .protocols(Setup.httpProtocol(baseUrl))
        .assertions(Setup.performanceAssertions());
  }
}
