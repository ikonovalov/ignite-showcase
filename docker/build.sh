#!/usr/bin/env bash

APP_VERSION=1.0.0

cd .. && mvn clean package && cd -

docker build -t "codeunited/zookeeper:3.4.10" ./zookeeper/ && \
echo "Zookeeper DONE" && \

cp ../target/showcase-${APP_VERSION}.jar ./ignite/showcase.jar && \
docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ./ignite/ && \
echo "Ready" && \
rm ./ignite/showcase.jar