#!/usr/bin/env bash

cd $(dirname "$0")
cd ..

./mvnw clean install -P sign-and-publish
./mvnw \
    --projects '!examples/domain1,!examples/maven-plugin-simple-example,!examples/maven-plugin-full-example' \
    deploy -P sign-and-publish
