#!/bin/bash
set -eu
cd "$(dirname $BASH_SOURCE)"

for d in */; do
  if [[ -f "${d}destroy.sh" ]]; then
    "${d}destroy.sh"
  fi
done
