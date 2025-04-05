#!/bin/bash

cd "$(dirname -- "$0")" || exit

CONTENT_TYPE_HDR='Content-Type:application/json'
URL=http://localhost:9000

curl -s -X DELETE -H $CONTENT_TYPE_HDR -d "@./event_table.json" "$URL/tables/foo"
curl -s -X DELETE -H $CONTENT_TYPE_HDR -d "@./schema.json" "$URL/schemas/foo"
