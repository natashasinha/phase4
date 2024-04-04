#!/bin/bash

cd "$(dirname -- "$0")"
#
# The pino schema and table creation can be done through the UI, but
# having it in a script makes it easier to demonstrate and document.
# 

#
# aliases to make the scripts easier to maintain, bash does not expand aliases, by default, if invoked from a terminal; so 
# make sure that alias expansion is enabled for bash.
#
shopt -s expand_aliases
#alias kt='kafka-topics --bootstrap-server localhost:9092'
alias kt='f() { docker exec -it pk_broker-1 sh -c "kafka-topics --bootstrap-server localhost:9092 $*"; unset -f f; }; f'
alias pinot='./pinot.sh'
alias dc='docker compose'

(cd local-kafka; dc up -d)
(cd local-redis; dc up -d)
(cd local; dc up -d --wait)
(cd local-kafka; dc up -d --wait)
(cd local-redis; dc up -d --wait)

#
# schemas must align to how the data is represented on the kafka topic.
#
pinot schema venue
pinot schema email
pinot schema phone
pinot schema customer
pinot schema address
pinot schema artist
pinot schema ticket
pinot schema event
pinot schema stream

#
# kafka topics must exist before the tables are created in pinot, since tables also define where the data is coming from
#
kt --create --if-not-exists --partitions 4 --topic data-demo-venues
kt --create --if-not-exists --partitions 4 --topic data-demo-emails
kt --create --if-not-exists --partitions 4 --topic data-demo-phones
kt --create --if-not-exists --partitions 4 --topic data-demo-customers
kt --create --if-not-exists --partitions 4 --topic data-demo-addresses
kt --create --if-not-exists --partitions 4 --topic data-demo-artists
kt --create --if-not-exists --partitions 4 --topic data-demo-tickets
kt --create --if-not-exists --partitions 4 --topic data-demo-events
kt --create --if-not-exists --partitions 4 --topic data-demo-streams

pinot table venue
pinot table email
pinot table phone
pinot table customer
pinot table address
pinot table artist
pinot table ticket
pinot table event
pinot table stream

./pinot2.sh schema event2
./pinot2.sh table event2
