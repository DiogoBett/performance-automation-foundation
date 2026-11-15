package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class GenericSpikeSimulation extends Simulation {
  {
    String baseUrl = Setup.performanceBaseUrl();

    setUp(
            Setup.requestScenario(Constants.SCENARIO_NAME_SPIKE)
                .injectOpen(atOnceUsers(Constants.PERFORMANCE_VIRTUAL_USERS)))
        .protocols(Setup.httpProtocol(baseUrl))
        .assertions(Setup.performanceAssertions());
  }
}
