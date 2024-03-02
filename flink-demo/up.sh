#!/bin/bash

set -e

cd "$(dirname -- "$0")/.."

(cd kafka-1; docker compose up -d)
(cd flink; docker compose up -d)

(cd kafka-1; docker compose up -d --wait)

