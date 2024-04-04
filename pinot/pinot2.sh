#!/bin/bash

cd "$(dirname -- "$0")" || exit

usage() {
  echo ""
  echo "Usage: $0 [-d|--delete|-c|--create] {schema|table} name"
  echo ""
}

if [ $# -lt 1 ]; then
  usage
  exit
fi

case $1 in
    -d|--delete)
      DELETE=true
      shift
      ;;
    -c|--create)
      DELETE=false
      shift
      ;;
    *)
      DELETE=false
      ;;
esac

OPERATION=$1
shift

NAME=$1
shift

NAME_PLURAL="$(./scripts/plural.sh "$NAME")"

URL=http://localhost:9000

CONTENT_TYPE_HDR='Content-Type:application/json'

case "${OPERATION}" in
  schema)
    if [ "$DELETE" == true ]; then
      curl -s -X DELETE -H $CONTENT_TYPE_HDR "$URL/schemas/$NAME" | jq
    else
      curl -s -X POST -H $CONTENT_TYPE_HDR -d "@./schemas/${NAME}.json" "$URL/schemas" | jq
    fi
    ;;
  table)
    if [ "$DELETE" == true ]; then
      curl -s -X DELETE -H $CONTENT_TYPE_HDR "$URL/tables/$NAME_PLURAL" | jq
    else
      TEMPLATE="$(<./scripts/table_template_st.json)"
      TEMPLATE="${TEMPLATE//\{\{TABLE\}\}/$NAME_PLURAL}"
      TEMPLATE="${TEMPLATE//\{\{SCHEMA\}\}/$NAME}"
      TEMPLATE="${TEMPLATE//\{\{TOPIC\}\}/$NAME_PLURAL}"

      echo "$TEMPLATE"

      curl -s -X POST -H $CONTENT_TYPE_HDR -d "$TEMPLATE" "$URL/tables" | jq
    fi
    ;;
  *)
    usage
esac
