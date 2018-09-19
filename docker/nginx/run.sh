#!/bin/bash

docker run --name nginx-proxy -p 8080:8080 -v ~/Work/Projects/coopel-all/docker/nginx/nginx.conf:/etc/nginx/nginx.conf:ro --rm nginx