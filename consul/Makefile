APP_VERSION=1.0.0

all: build up

build:
	cd .. && mvn clean package && cd - && \
	cp ../target/showcase-${APP_VERSION}.jar ../docker/showcase.jar && \
	docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ../docker/ && \
	rm ../docker/showcase.jar
up:
	docker-compose up
down:
	docker-compose down
clean: down
	docker volume prune --force

visor:
	docker-compose exec ignite-server bash -c './apache-ignite-fabric-2.3.0-bin/bin/ignitevisorcmd.sh'
