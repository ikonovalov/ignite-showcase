FROM openjdk:8-jre-alpine
LABEL maintainer="konovalov84@gmail.com"

ADD showcase.jar /opt/ignite/showcase.jar

VOLUME ["/var/ignite/ds"]

CMD java -XX:+DisableExplicitGC -Xms512m -Xmx1024m -XX:MaxDirectMemorySize=128m -jar /opt/ignite/showcase.jar

EXPOSE 8080 11211 47100 47500 49112
