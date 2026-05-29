#!/bin/sh
set -e

LIBS_DIR="/app/libs" # points to the directory mounted in docker compose
mkdir -p "$LIBS_DIR"

RELEASE_URL="https://github.com/apolicky/ice2/releases/download/v0.0.0"
LIBS="HyFD-1.2-SNAPSHOT-clean.jar SPIDER-1.2-SNAPSHOT-clean.jar"

# If libs dir is empty, download JARs
if [ -z "$(ls -A "$LIBS_DIR")" ]; then
  echo "libs directory is empty — downloading dependencies..."

  # Example files — adjust names & URLs
  for lib in $LIBS; do
    curl -L -o "$LIBS_DIR/$lib" "$RELEASE_URL/$lib" 
  done

  echo "libs downloaded successfully."
else
  echo "libs directory already populated, skipping download."
fi