#!/bin/sh

set -e
cd "$(dirname -- "$0")"

(cd application; gradle build shadowJar; cp ./build/libs/flink_demo_application-0.1.0-all.jar ../../flink/jars)

