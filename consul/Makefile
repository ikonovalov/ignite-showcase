APP_VERSION=1.0.0

all: build up

build:
	cd .. && mvn clean package && cd - && \
	cp ../target/showcase-${APP_VERSION}.jar ./ignite/showcase.jar && \
	docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ./ignite/ && \
	rm ./ignite/showcase.jar
up:
	docker-compose up
down:
	docker-compose down
