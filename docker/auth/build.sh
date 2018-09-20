#!/bin/bash

mvn clean install -Dmaven.test.skip=true
cp ../../auth/target/auth-*.jar auth.jar
docker build -t auth:dev .