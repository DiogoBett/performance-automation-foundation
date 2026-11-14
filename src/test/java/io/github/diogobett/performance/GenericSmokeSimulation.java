package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class GenericSmokeSimulation extends Simulation {
  {
    String baseUrl = GenericSimulationSupport.baseUrl(true);

    setUp(GenericSimulationSupport.scenarioForEnvironment("Smoke").injectOpen(atOnceUsers(1)))
        .protocols(GenericSimulationSupport.httpProtocol(baseUrl))
        .assertions(GenericSimulationSupport.performanceAssertions());
  }
}
