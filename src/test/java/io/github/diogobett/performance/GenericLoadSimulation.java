package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;

public class GenericLoadSimulation extends Simulation {
  {
    String baseUrl = GenericSimulationSupport.baseUrl(false);

    setUp(
            GenericSimulationSupport.scenarioForEnvironment("Load")
                .injectOpen(rampUsers(50).during(Duration.ofMinutes(2))))
        .protocols(GenericSimulationSupport.httpProtocol(baseUrl))
        .assertions(GenericSimulationSupport.performanceAssertions());
  }
}
