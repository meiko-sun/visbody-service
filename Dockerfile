FROM daocloud.io/java:latest
VOLUME /tmp
ADD target/visbody-service-0.1.0.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar