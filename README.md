## performance-automation-foundation

### Overview:
* This project serves as a foundation for automated performance testing using Java and Gatling;
* The example simulations exercise the QuickPizza API with login and authenticated pizza recommendation requests;
* Gatling generates an HTML performance report for each simulation run.

### Requirements
* Java (17 or above);
* Maven (3.9 or above).

### Guidelines:
* Target:
  * Start QuickPizza locally with `docker run --rm -it -p 3333:3333 ghcr.io/grafana/quickpizza-local:latest`;
  * Create a QuickPizza account through the app or `POST /api/users`;
  * Set `QUICKPIZZA_USERNAME` and `QUICKPIZZA_PASSWORD` to the account credentials;
  * Simulations only accept `localhost`, `127.0.0.1`, or `::1` as the base URL.
* Test Run:
  * Run the one-user smoke simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaSmokeSimulation'`;
  * Run 50 users ramped over two minutes with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaLoadSimulation'`;
  * Run the 50-user spike simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.QuickPizzaSpikeSimulation'`;
  * Each simulation checks for p95 response time below 500 ms and fewer than 1% failed requests.
* Formatting:
  * Run `mvn spotless:apply` to format Java sources;
  * Run `mvn spotless:check` to check formatting without changing files.

### Scenario Coverage
Each simulation exercises the same authenticated request flow:
* **Login**: authenticate with the configured QuickPizza user and extract the response token;
* **Pizza recommendation**: reuse the token to request a pizza recommendation and verify the response;
* **Smoke**: run the flow with one virtual user;
* **Load**: ramp up to 50 virtual users over two minutes;
* **Spike**: start 50 virtual users at once.

### Key Components
* Gatling - Defines virtual users, HTTP requests, load profiles, checks, and performance assertions;
* QuickPizza - A local demo application used as the controlled performance-test target;
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
* How do I change the target URL?
  * Set `-DbaseUrl=http://localhost:3333` when running Gatling;
  * Non-loopback URLs are rejected so performance traffic stays on a local target.
* Where can I find Gatling's report?
  * Locally, open the generated report under `target/gatling/`;
  * In GitHub Actions, download the `gatling-report` workflow artifact.
