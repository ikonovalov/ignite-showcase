FROM openjdk:8-jre-alpine
LABEL maintainer="konovalov84@gmail.com"

COPY showcase.jar /opt/ignite/showcase.jar

VOLUME ["/var/ignite/ds"]

ENTRYPOINT java -XX:+UseG1GC -XX:+DisableExplicitGC -Xms512m -Xmx1024m -XX:MaxDirectMemorySize=512m -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true -jar /opt/ignite/showcase.jar

EXPOSE 28080 11211 47100 47500 49112
