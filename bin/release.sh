#!/usr/bin/env bash

cd $(dirname "$0")
cd ..

./mvnw release:prepare && ./mvnw release:clean
