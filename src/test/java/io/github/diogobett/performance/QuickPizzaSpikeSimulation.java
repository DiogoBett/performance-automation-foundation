package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class QuickPizzaSpikeSimulation extends Simulation {
  {
    String baseUrl = QuickPizzaSimulationSupport.baseUrl();

    setUp(
            QuickPizzaSimulationSupport.pizzaRecommendationScenario("QuickPizza spike")
                .injectOpen(atOnceUsers(50)))
        .protocols(QuickPizzaSimulationSupport.httpProtocol(baseUrl))
        .assertions(QuickPizzaSimulationSupport.performanceAssertions());
  }
}
