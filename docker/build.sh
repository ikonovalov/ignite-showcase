#!/usr/bin/env bash

APP_VERSION=1.0.0

cd .. && mvn clean package && cd -

cp ../target/showcase-${APP_VERSION}.jar ./ignite/showcase.jar && \
docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ./ignite/ && \
echo "====================================================================== Ignite DONE" && \
rm ./ignite/showcase.jar