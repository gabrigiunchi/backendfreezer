#!/usr/bin/env bash

# gcloud auth activate-service-account --key-file service-account-key.json
# gcloud config set project freezer-276212
mvn clean
mvn package -DskipTests -e