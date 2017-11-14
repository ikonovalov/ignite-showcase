
#### How to build

Build docker image with  application
```bash
make build
```
Start
```bash
make up
```

Stop
```bash
make down
```

_Deploy grid instance (optional)_
```bash
make deploy
```

### Consul showcase links

#### Hystrix
 * Dashboard http://data-grid.127.0.0.1.xip.io/hystrix
 * Stream(internal link!) http://localhost:8080/hystrix.stream

#### Health
 * The SpringBoot health [http://data-grid.127.0.0.1.xip.io/health.json](http://data-grid.127.0.0.1.xip.io/health.json)  

#### Consul & HAProxy
* Consul dashboard [http://consul-admin.infra.127.0.0.1.xip.io](http://consul-admin.infra.127.0.0.1.xip.io)
* HAProxy dashboard [http://proxy-admin.infra.127.0.0.1.xip.io](http://consul-admin.infra.127.0.0.1.xip.io)

### Mesos
* Mesos dashboard [http://localhost:5050/#/](http://localhost:5050/#/)
* Marathon dashboard [http://localhost:8081/#/](http://localhost:8081/)
* Exhibitor dashboard [http://localhost:8080/exhibitor/v1/ui/index.html](http://localhost:8080/exhibitor/v1/ui/index.html)

### Kubernetes
TODO
