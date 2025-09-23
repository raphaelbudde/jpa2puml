#!/usr/bin/env bash

cd $(dirname "$0")
cd ..

./mvnw clean install -P sign-and-publish
./mvnw --projects '!examples/domain1,!examples/maven-plugin-test' deploy -P sign-and-publish
