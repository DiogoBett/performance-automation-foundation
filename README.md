## performance-automation-foundation

### Overview:
* This project serves as a foundation for automated performance testing using Java and Gatling;
* The `dev` environment uses QuickPizza as an example API, with login and authenticated pizza recommendation requests;
* The `preprod` and `prod` environments use a configurable generic HTTP GET smoke check, so they do not need to be QuickPizza instances;
* Gatling generates an HTML performance report for each simulation run.

### Requirements
* Java (17 or above);
* Maven (3.9 or above).

### Guidelines:
* Target:
  * Start QuickPizza locally with `docker run --rm -it -p 3333:3333 ghcr.io/grafana/quickpizza-local:latest`;
  * Create a QuickPizza account through the app or `POST /api/users`;
  * Set `QUICKPIZZA_USERNAME` and `QUICKPIZZA_PASSWORD` to the account credentials;
  * The `dev` environment defaults to `http://localhost:3333` and requires a loopback URL;
  * The `preprod` and `prod` environments require an explicitly configured base URL and smoke path. Only the one-user smoke simulation is allowed for these environments.
* Test Run:
  * Run the one-user smoke simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.GenericSmokeSimulation'`;
  * Run 50 users ramped over two minutes with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.GenericLoadSimulation'`;
  * Run the 50-user spike simulation with `mvn gatling:test '-Dgatling.simulationClass=io.github.diogobett.performance.GenericSpikeSimulation'`;
  * Select `preprod` or `prod` with `-Denvironment=<environment>`, and configure `-DbaseUrl=<target-url>` and `-DsmokePath=<path>`;
  * Each simulation checks for p95 response time below 500 ms and fewer than 1% failed requests.
* Formatting:
  * Run `mvn spotless:apply` to format Java sources;
  * Run `mvn spotless:check` to check formatting without changing files.

### Scenario Coverage
The simulation classes select a scenario according to the chosen environment:
* **DEV smoke**: log in to QuickPizza, extract the token, and request a pizza recommendation with one virtual user;
* **DEV load**: ramp up to 50 virtual users over two minutes against local QuickPizza;
* **DEV spike**: start 50 virtual users at once against local QuickPizza;
* **PREPROD/PROD smoke**: send one unauthenticated GET request to the configured path and check the expected HTTP status.

### Key Components
* Gatling - Defines virtual users, HTTP requests, load profiles, checks, and performance assertions;
* QuickPizza - The local DEV example application used as a controlled performance-test target;
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
