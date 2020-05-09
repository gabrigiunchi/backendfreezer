#!/usr/bin/env bash

# gcloud auth activate-service-account --key-file service-account-key.json
# gcloud config set project freezer-276212
gcloud activa-service-account --key-file service-account-key.json
mvn clean
mvn package -DskipTests -e
mvn appengine:deploy
