FROM java:openjdk-8u111-alpine

RUN mkdir /app

WORKDIR /app

COPY target/cake-0.0.1-SNAPSHOT.jar /app

EXPOSE 3006

CMD [ "java", "-jar", "cake-0.0.1-SNAPSHOT.jar" ]