#!/bin/bash

cd ../..
mvn clean install -Dmaven.test.skip=true
cp auth/target/auth-*.jar docker/auth/auth.jar


docker build -t auth:dev ./docker/auth