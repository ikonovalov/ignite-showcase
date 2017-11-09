APP_VERSION=1.0.0

all: build up

build:
	cd .. && mvn clean package && cd -
up:
	docker-compose up
down:
	docker-compose down
