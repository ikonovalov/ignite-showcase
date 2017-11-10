APP_VERSION=1.0.0

all: build up

minikube:
    minikube start --memory=4096
    minikube dashboard

build:
	cd .. && mvn clean package && cd - && \
    cp ../target/showcase-${APP_VERSION}.jar ./ignite/showcase.jar && \
    eval $(docker-machine env) && \
    docker build -t "codeunited/ignite-showcase:${APP_VERSION}" ../docker/ignite/ && \
    rm ./ignite/showcase.jar

deploy:
	kubectl apply -f deployment.yaml

sample:
    kubectl apply -f ignite-example.yaml

ab:
    ab -n10000 -c10 http://$(minikube ip):30300/quest/1