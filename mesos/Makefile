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

################################################
#Application tasks

MARATHON_DG_URL=http://localhost:8081/v2/apps/data-grid

deploy:
	curl -XPUT ${MARATHON_DG_URL} --upload-file marathon-data-grid.json
undeploy:
	curl -XDELETE ${MARATHON_DG_URL}
restart:
	curl -XPOST ${MARATHON_DG_URL}/restart
show:
	curl -XGET ${MARATHON_DG_URL}


