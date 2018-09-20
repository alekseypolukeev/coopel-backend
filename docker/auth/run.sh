#!/bin/bash

docker run \
  -d --rm \
  -p 22022:22022 \
  --name auth-dev \
  auth:dev --spring.profiles.active=dev