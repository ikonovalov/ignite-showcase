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

stress:
	ab -n1000 -c100 data-grid.127.0.0.1.xip.io/services/execution-id
