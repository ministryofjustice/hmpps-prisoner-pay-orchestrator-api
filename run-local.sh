#!/usr/bin/env bash

# Load environment variables from .env into the current shell environment.
# Using `set -a` + `source` handles values containing spaces, quotes and '='
if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

# Run the application with local profile active
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
