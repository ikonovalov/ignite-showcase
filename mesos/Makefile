APP_VERSION=1.0.0

all: build up

up:
	docker-compose up

build:
	cd .. && mvn clean package && cd - && \
	cp ../target/showcase-${APP_VERSION}.jar ../docker/showcase.jar && \
	docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ../docker/ && \
	rm ../docker/showcase.jar

down:
	docker-compose down

clean: down
	docker volume prune --force

deploy:
	curl -XPUT http://localhost:8081/v2/apps/data-grid --upload-file marathon-data-grid.json
undeploy:
	curl -XDELETE http://localhost:8081/v2/apps/data-grid
restart:
	curl -XPOST http://localhost:8081/v2/apps/data-grid/restart
show:
	curl -XGET http://localhost:8081/v2/apps/data-grid


