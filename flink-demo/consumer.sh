#!/bin/sh

if [ $# == 0 ]; then
  echo "usage $0 <topic>"
  exit
fi

docker exec -it ddc_kafka1_schema-registry sh -c "kafka-avro-console-consumer \
        --bootstrap-server broker-1:9092 \
        --property schema.registry.url=\"http://localhost:8081\" \
        --property print.key=true \
        --property key.separator=\"|\" \
        --from-beginning \
        --skip-message-on-error \
        --topic $@"
