# Performance Automation Foundation

A Java 17 and Gatling foundation for repeatable performance tests, configurable local targets, and HTML reports.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- Docker, to run QuickPizza locally

## Target

`test-api.k6.io` is no longer a suitable demo target: its project is deprecated, and the one-request check in November 2026 returned `302 Found` with a redirect to QuickPizza. The target list also recommends QuickPizza specifically for performance testing. These starter simulations therefore use QuickPizza's local API at `http://localhost:3333`.

The simulations only accept loopback base URLs (`localhost`, `127.0.0.1`, or `::1`), including the one-user smoke run. This keeps load traffic off shared public services.

Start the local QuickPizza application in a separate terminal:

```shell
docker run --rm -it -p 3333:3333 ghcr.io/grafana/quickpizza-local:latest
```

Create a QuickPizza user through its UI or `POST /api/users`, then provide that account's credentials through environment variables:

```powershell
$env:QUICKPIZZA_USERNAME = "your-local-user"
$env:QUICKPIZZA_PASSWORD = "your-local-password"
```

The simulations log in with the configured account, extract the token, and use it to request a pizza recommendation. Credentials are not stored in the repository or printed by the simulations.

## Run simulations

Run one virtual user through the login and pizza recommendation flow:

```shell
mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaSmokeSimulation'
```

Ramp up to 50 users over two minutes against the local instance:

```shell
mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaLoadSimulation'
```

Start 50 users at once against the local instance:

```shell
mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaSpikeSimulation'
```

The base URL can be changed with `'-DbaseUrl=http://localhost:3333'`. Load and spike simulations reject non-loopback URLs. Each simulation asserts a p95 response time below 500 ms and fewer than 1% failed requests.

Gatling writes an HTML report under `target/gatling/` for each run. These simulations are deliberately not run against the public QuickPizza host or the deprecated `test-api.k6.io`.

## Allure compatibility

Gatling does not produce Cucumber JSON: its results are virtual-user request timings, response-time distributions, and failures, rather than feature/scenario/step outcomes. Allure's documented Cucumber-JVM integration consumes Cucumber results, and there is no maintained Gatling adapter published on Maven Central. Converting Gatling runs into synthetic Cucumber scenarios would lose performance detail and misrepresent the results, so the Gatling HTML report remains the source of truth.

If an Allure dashboard is needed later, an explicit Allure result per simulation with Gatling summary data attached is a better fit than Cucumber JSON. It would complement—not replace—the Gatling report.

## Project layout

```text
src/test/java/io/github/diogobett/performance/
  QuickPizzaSimulationSupport.java
  QuickPizzaSmokeSimulation.java
  QuickPizzaLoadSimulation.java
  QuickPizzaSpikeSimulation.java
```

## Versions

Gatling `3.16.0`, Gatling Maven Plugin `4.21.12`, and Maven Compiler Plugin `3.16.0` are the latest stable Maven Central releases checked for this setup.

## References

- [QuickPizza](https://github.com/grafana/quickpizza)
- [QuickPizza local Docker instructions](https://github.com/grafana/quickpizza#run-locally-with-docker)
- [Awesome Sites To Test On](https://github.com/BMayhew/awesome-sites-to-test-on)
- [Gatling documentation](https://docs.gatling.io/)
- [Allure Cucumber-JVM integration](https://allurereport.org/docs/cucumberjvm/)
