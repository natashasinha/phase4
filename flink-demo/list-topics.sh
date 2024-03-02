#!/bin/sh

docker exec -it ddc_kafka1_broker-1 sh -c "kafka-topics \
        --bootstrap-server localhost:9092 \
        --list"
