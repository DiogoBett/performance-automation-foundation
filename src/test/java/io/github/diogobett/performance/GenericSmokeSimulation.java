package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class GenericSmokeSimulation extends Simulation {
  {
    String baseUrl = Setup.baseUrl();

    setUp(Setup.smokeScenario(Constants.SCENARIO_NAME_SMOKE).injectOpen(atOnceUsers(1)))
        .protocols(Setup.httpProtocol(baseUrl))
        .assertions(Setup.smokeAssertions());
  }
}
