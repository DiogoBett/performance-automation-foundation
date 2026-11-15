## performance-automation-foundation

### Overview:
* This project serves as a foundation for automated performance testing using Java and Gatling;
* Gatling generates an HTML performance report for each simulation run.

### Requirements
* Java (17 or above);
* Maven (3.9 or above).

### Guidelines:
* Test Run:
  * Run the one-user public QuickPizza smoke simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.simulations.GenericSmokeSimulation'`;
  * Run the five-user QuickPizza load simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.simulations.GenericLoadSimulation'`;
  * Run the five-user QuickPizza spike simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.simulations.GenericSpikeSimulation'`;
  * The `dev` environment uses the public QuickPizza endpoint for all three small-scale scenarios;
  * The `preprod` and `prod` environments are placeholders and are not configured.
* Formatting:
  * Run `mvn spotless:apply` to format Java sources;
  * Run `mvn spotless:check` to check formatting without changing files.

### Scenario Coverage
* **Smoke**: a brief validation that the application responds correctly under minimal load to catch obvious issues early;
* **Load**: a sustained test that increases traffic to measure responsiveness, throughput, and stability under expected usage;
* **Spike**: a sudden burst of traffic to assess how the system handles abrupt demand spikes and recovers afterward.

### Key Components
* Gatling - Defines virtual users, HTTP requests, load profiles, checks, and performance assertions;
* Maven - Manages dependencies, compilation, simulation execution, and report generation;
* Spotless - Formats Java sources with Google Java Format and checks formatting during Maven verification.

### Dependencies
* `io.gatling`
  * `gatling-core-java`
  * `gatling-http-java`
* `io.gatling.highcharts`
  * `gatling-charts-highcharts`

### Documentation
* Gatling - https://docs.gatling.io/
* QuickPizza - https://github.com/grafana/quickpizza

### FAQ / Troubleshooting
* Where can I find Gatling's report?
  * Locally, open the generated report under `target/gatling/`;
  * In GitHub Actions, download the `gatling-report` workflow artifact.
