#!/bin/sh

set -e
cd "$(dirname -- "$0")"

docker exec -it flink_jobmanager sh -c "flink run -p 4 --detached /jars/flink_demo_application-0.1.0-all.jar"
