#!/bin/bash
set -eu
cd "$(dirname $BASH_SOURCE)"

for d in */; do
  if [[ -f "${d}stop.sh" ]]; then
    "${d}stop.sh"
  fi
done
