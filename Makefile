APP_VERSION=1.0.0

build:
	mvn clean package && \
	cp ./target/showcase-${APP_VERSION}.jar ./docker/showcase.jar && \
	docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ./docker/ && \
	rm ./docker/showcase.jar

deploy-on-heroku:
	git push heroku master && heroku ps:scale web=1 && curl https://pure-plateau-77452.herokuapp.com/health | jq