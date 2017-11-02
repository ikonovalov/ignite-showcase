FROM openjdk:8-jre-alpine
LABEL maintainer="konovalov84@gmail.com"


ARG MIRROR=http://apache.mirrors.pair.com
ARG VERSION=3.4.10

LABEL name="zookeeper" version=$VERSION

RUN apk add --no-cache wget bash \
    && mkdir -p /opt/zookeeper \
    && wget -q -O - $MIRROR/zookeeper/zookeeper-$VERSION/zookeeper-$VERSION.tar.gz \
      | tar -xzC /opt/zookeeper --strip-components=1 \
    && cp /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg \
    && mkdir -p /tmp/zookeeper

EXPOSE 2181 2888 3888

WORKDIR /opt/zookeeperdo

VOLUME ["/opt/zookeeper/conf", "/tmp/zookeeper"]

ENTRYPOINT ["/opt/zookeeper/bin/zkServer.sh"]
CMD ["start-foreground"]