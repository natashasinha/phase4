#!/bin/sh

set -e

cd "$(dirname -- "$0")"

echo "setup started."
echo ""

echo "installing avro specification to connect cluster for datagen"
echo ""

cp -r ./application/src/main/avro/order.avsc ../kafka-1/connect-data/datagen

echo "creating topics"
echo ""
#kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --partitions 4 --topic datagen.orders
#kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --partitions 4 --topic purchase-orders

docker exec -it ddc_kafka1_broker-1 sh -c "kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --partitions 4 --topic datagen.orders"
docker exec -it ddc_kafka1_broker-1 sh -c "kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --partitions 4 --topic purchase-orders"


echo "creating connector 'datagen-orders'"
echo ""
./connect.sh create ./connectors/datagen-orders.json

echo "launch application"
echo ""
(cd application; gradle build shadowJar; cp ./build/libs/flink_application-all.jar ../../flink/jars)

docker exec -it flink_jobmanager sh -c "flink run -p 4 --detached /jars/flink_application-all.jar"

echo ""
echo "setup completed."
