FROM openjdk:8-jre-alpine


ENV JAVA_OPTS "-XX:+UseG1GC -XX:+DisableExplicitGC -Xms256m -Xmx512m -XX:MaxDirectMemorySize=128m"

ADD showcase.jar /opt/ignite/showcase.jar


EXPOSE 8080 11211 47100 47500 49112

CMD java $JAVA_OPTS -jar /opt/ignite/showcase.jar
