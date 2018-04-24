all: minikube build deploy

mini-start:
	minikube start --memory=4096

mini-start-none-driver:
	sudo minikube start --vm-driver=none && \
	sudo chown -R $USER:$USER ~/.minikube && \
	sudo chown -R $USER:$USER ~/.kube

mini-delete:
	sudo minikube delete

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
