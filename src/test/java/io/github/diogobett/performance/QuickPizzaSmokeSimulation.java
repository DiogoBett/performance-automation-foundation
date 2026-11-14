package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

import io.gatling.javaapi.core.Simulation;

public class QuickPizzaSmokeSimulation extends Simulation {
  {
    String baseUrl = QuickPizzaSimulationSupport.baseUrl();

    setUp(
            QuickPizzaSimulationSupport.pizzaRecommendationScenario("QuickPizza smoke")
                .injectOpen(atOnceUsers(1)))
        .protocols(QuickPizzaSimulationSupport.httpProtocol(baseUrl))
        .assertions(QuickPizzaSimulationSupport.performanceAssertions());
  }
}
