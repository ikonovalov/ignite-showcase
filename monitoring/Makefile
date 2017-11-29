all: build up

up:
	docker-compose up

down:
	docker-compose down
	sudo chown -R `id -u`:`id -g` .

clean: down
	docker volume prune --force

