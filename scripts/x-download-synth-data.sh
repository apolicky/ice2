#!/bin/sh
set -e

INPUTS_DIR="/app/inputs" # points to the directory mounted in docker compose
mkdir -p "$INPUTS_DIR"

RELEASE_URL="https://github.com/apolicky/ice2/releases/download/v0.0.2"
# choose whether to use only synth or all
# INPUTS="synth-datasets.zip"
INPUTS="synth-datasets.zip yelp1.zip formula1-f3.zip"

# If inputs dir is empty, download synth datasets, skip otherwise
if [ -z "$(ls -A "$INPUTS_DIR")" ]; then
  echo "inputs directory is empty — downloading dependencies..."

  for input in $INPUTS; do
    curl -L -o "/tmp/$input" "$RELEASE_URL/$input"
    unzip -o "/tmp/$input" -d "$INPUTS_DIR"
  done

  echo "inputs downloaded successfully."
else
  echo "inputs directory already populated, skipping download."
fi