#!/bin/sh
set -e

cd "$(dirname "$0")"
gradle assemble

. ./.classpath.sh

#
#
#

export KAFKA_STREAMS_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_STREAMS_APPLICATION_ID=sample-streams-3

export IN="data-demo-.*"

export IN_TOPIC=data-demo-customers
export OUT_TOPIC=OUT-customers

#
#
#

MAIN="org.msse.attachschema.streams.Main"

java -cp "${CP}" "$MAIN" "$@"

#DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
#java -cp "${CP}" ${DEBUG} "$MAIN" "$@"

