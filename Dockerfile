FROM openjdk:17-jdk-slim

RUN mkdir /app

WORKDIR /app

COPY target/cake-0.0.1-SNAPSHOT.jar /app

EXPOSE 3009

CMD [ "java", "-jar", "cake-0.0.1-SNAPSHOT.jar" ]