#!/bin/bash

cd "$(dirname -- "$0")" || exit

CONTENT_TYPE_HDR='Content-Type:application/json'
URL=http://localhost:9000

# create schema
curl -s -X POST -H $CONTENT_TYPE_HDR -d "@./schema.json" "$URL/schemas"
curl -s -X POST -H $CONTENT_TYPE_HDR -d "@./table.json" "$URL/tables"
