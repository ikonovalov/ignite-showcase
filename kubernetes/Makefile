
all: minikube build deploy

minikube:
	minikube start --memory=4096 && sleep 16 && minikube dashboard

build:
	cd ../ && mvn clean package && cd kubernetes && \
	cp ../target/showcase*.jar ./showcase.jar && \
	eval $$(minikube docker-env) && \
	docker build -t "codeunited/ignite-showcase" . && \
	rm ./showcase.jar

deploy:
	kubectl apply -f deployment.yaml

sample:
	kubectl apply -f ignite-example.yaml

ab:
	ab -n10000 -c10 http://$$(minikube ip):30300/quest/1
