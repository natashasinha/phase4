#!/bin/bash

set -e
cd "$(dirname -- "$0")"

function installPlugin() {
    connector=$1
    DIRECTORY=${connector%%;*}
    PLUGIN=${connector#*;}
    touch ../tmp/connect-distributed.properties
    echo "plugin : $connector"
    if [ ! -d "../kafka-1/connect-plugins/$DIRECTORY" ]; then
      echo "installing plugin : $connector"
      confluent-hub install --worker-configs ../tmp/connect-distributed.properties --component-dir "../kafka-1/connect-plugins" --no-prompt "${PLUGIN}"
      echo ""
    else
      echo "already installed: $DIRECTORY"
    fi
    rm ../tmp/connect-distributed.properties
}

function installPlugins() {
  declare -a CONNECTORS="$@"
  for connector in ${CONNECTORS[@]} ; do
    installPlugin $connector
  done
}

#
#
#


echo "installing connectors"

declare -a CONNECTORS=(
  "confluentinc-kafka-connect-datagen;confluentinc/kafka-connect-datagen:latest"
)

installPlugins "${CONNECTORS[@]}"

echo "installing avro specification"

cp -r ./application/src/main/avro/order.avsc ../connect/data/datagen

echo "installation of connectors and needed data for connectors completed."
