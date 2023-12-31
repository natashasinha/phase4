#!/bin/bash

cd "$(dirname -- "$0")"

shopt -s expand_aliases
alias dc='docker compose'

(cd local; dc down -v)
