package io.github.diogobett.performance.simulations;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;
import io.github.diogobett.performance.utility.Constants;
import io.github.diogobett.performance.utility.Setup;

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
