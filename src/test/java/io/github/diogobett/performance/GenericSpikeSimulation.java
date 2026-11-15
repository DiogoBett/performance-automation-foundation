package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class GenericSpikeSimulation extends Simulation {
  {
    String baseUrl = Setup.loopbackBaseUrl();

    setUp(Setup.requestScenario(Constants.SCENARIO_NAME_SPIKE).injectOpen(atOnceUsers(50)))
        .protocols(Setup.httpProtocol(baseUrl))
        .assertions(Setup.performanceAssertions());
  }
}
