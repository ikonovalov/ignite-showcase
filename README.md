
#### How to build

Build docker image with  application
```bash
make build
```
Start
```bash
make up
```

Shutdown
```bash
make down
```

_Deploy grid instance (Marathon option)_
```bash
make deploy
```

## Consul

#### Hystrix
 * Dashboard http://data-grid.127.0.0.1.xip.io/hystrix
 * Stream(internal link!) http://localhost:8080/hystrix.stream

#### Health
 * The SpringBoot health [http://data-grid.127.0.0.1.xip.io/health.json](http://data-grid.127.0.0.1.xip.io/health.json)  

#### Consul & HAProxy
* Consul dashboard [http://consul-admin.infra.127.0.0.1.xip.io](http://consul-admin.infra.127.0.0.1.xip.io)
* HAProxy dashboard [http://proxy-admin.infra.127.0.0.1.xip.io](http://consul-admin.infra.127.0.0.1.xip.io)



## Mesosphere
* Exhibitor/ZK UI [http://localhost:8080/exhibitor/v1/ui/index.html](http://localhost:8080/exhibitor/v1/ui/index.html)
* Mesos dashboard [http://mesos.127.0.0.1.xip.io:5050/#/](http://mesos.127.0.0.1.xip.io:5050/#/)
* Marathon dashboard [marathon.127.0.0.1.xip.io:8081/#/](marathon.127.0.0.1.xip.io:8081)
* Marathon-LB (HAProxy on steroids)
    * [http://ha.127.0.0.1.xip.io:9090/haproxy?stats](http://ha.127.0.0.1.xip.io:9090/haproxy?stats) 
    * [http://ha.127.0.0.1.xip.io:9090/_haproxy_getconfig](http://ha.127.0.0.1.xip.io:9090/_haproxy_getconfig) 

Warm up load-balancer 
```bash 
ab -n200 -c60 http://dg.127.0.0.1.xip.io/quest/1
```

## Kubernetes
TODO
