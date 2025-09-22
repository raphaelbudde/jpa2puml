#!/usr/bin/env bash

cd $(dirname "$0")
pwd

function jpa2puml() {
    java  --enable-native-access=ALL-UNNAMED -jar ../jpa2puml/target/jpa2puml-1.2.0-SNAPSHOT-jar-with-dependencies.jar $@
}

jpa2puml -o ../images/domain1.png ../examples/domain1/target/classes/
jpa2puml -o ../images/domain1.svg ../examples/domain1/target/classes/
jpa2puml -i -e -o ../images/domain1-full.png ../examples/domain1/target/classes/
jpa2puml -i -e -o ../images/domain1-full.svg ../examples/domain1/target/classes/
