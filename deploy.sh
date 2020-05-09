#!/usr/bin/env bash

gcloud auth activate-service-account --key-file service-account-key.json
mvn clean
mvn package -DskipTests -e
mvn appengine:deploy
