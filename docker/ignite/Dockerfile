FROM openjdk:8-jre-alpine
LABEL maintainer="konovalov84@gmail.com"

COPY showcase.jar /opt/ignite/showcase.jar

CMD java -XX:+DisableExplicitGC -Xms512m -Xmx1024m -XX:MaxDirectMemorySize=128m -jar /opt/ignite/showcase.jar

EXPOSE 8080 11211 47100 47500 49112
