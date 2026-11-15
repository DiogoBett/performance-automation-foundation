import csv
import json
import sys
import uuid
from pathlib import Path


SCENARIOS = {
    "GenericSmokeSimulation": "Smoke",
    "GenericLoadSimulation": "Load",
    "GenericSpikeSimulation": "Spike",
}


def main():
    if len(sys.argv) != 3:
        raise SystemExit(
            "Usage: generate_allure_results.py <simulation-results.tsv> <output-directory>"
        )

    input_path = Path(sys.argv[1])
    output_directory = Path(sys.argv[2])
    output_directory.mkdir(parents=True, exist_ok=True)
    seen = set()

    with input_path.open(newline="", encoding="utf-8") as results_file:
        for row in csv.reader(results_file, delimiter="\t"):
            if len(row) != 4:
                raise ValueError(f"Expected four tab-separated fields, got: {row!r}")

            simulation, status, start, stop = row
            if simulation not in SCENARIOS:
                raise ValueError(f"Unsupported Gatling simulation: {simulation}")
            if simulation in seen:
                raise ValueError(f"Duplicate Gatling simulation result: {simulation}")
            if status not in {"passed", "failed"}:
                raise ValueError(f"Unsupported Allure status: {status}")

            start_time = int(start)
            stop_time = int(stop)
            if stop_time < start_time:
                raise ValueError(f"Invalid timestamps for {simulation}: {start}, {stop}")

            result = {
                "uuid": str(uuid.uuid4()),
                "name": SCENARIOS[simulation],
                "fullName": f"io.github.diogobett.performance.{simulation}",
                "status": status,
                "stage": "finished",
                "start": start_time,
                "stop": stop_time,
                "labels": [
                    {"name": "suite", "value": "Performance Scenarios"},
                    {"name": "framework", "value": "Gatling"},
                    {"name": "language", "value": "Java"},
                ],
            }
            if status == "failed":
                result["statusDetails"] = {
                    "message": "Gatling simulation failed. See the Gatling HTML report and CI logs."
                }

            output_path = output_directory / f"{result['uuid']}-result.json"
            output_path.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
            seen.add(simulation)

    missing = set(SCENARIOS) - seen
    if missing:
        raise ValueError(f"Missing Gatling simulation results: {', '.join(sorted(missing))}")


if __name__ == "__main__":
    main()
