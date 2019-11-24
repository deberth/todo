FROM java:8-jre

WORKDIR /data

ADD target/todo-1.0-SNAPSHOT.jar /data/todo-1.0.jar
ADD src/main/resources/config.yml /data/config.yml
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "todo-1.0.jar", "server", "config.yml"]