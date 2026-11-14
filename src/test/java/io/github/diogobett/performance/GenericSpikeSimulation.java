package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class GenericSpikeSimulation extends Simulation {
  {
    String baseUrl = GenericSimulationSupport.baseUrl(false);

    setUp(GenericSimulationSupport.scenarioForEnvironment("Spike").injectOpen(atOnceUsers(50)))
        .protocols(GenericSimulationSupport.httpProtocol(baseUrl))
        .assertions(GenericSimulationSupport.performanceAssertions());
  }
}
