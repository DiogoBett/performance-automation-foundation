package io.github.diogobett.performance;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;

import io.gatling.javaapi.core.Simulation;
import java.time.Duration;

public class QuickPizzaLoadSimulation extends Simulation {
  {
    String baseUrl = QuickPizzaSimulationSupport.baseUrl();

    setUp(
            QuickPizzaSimulationSupport.pizzaRecommendationScenario("QuickPizza ramp load")
                .injectOpen(rampUsers(50).during(Duration.ofMinutes(2))))
        .protocols(QuickPizzaSimulationSupport.httpProtocol(baseUrl))
        .assertions(QuickPizzaSimulationSupport.performanceAssertions());
  }
}
