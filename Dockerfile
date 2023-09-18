FROM openjdk:17.0.1-jdk-slim

ENV APP_NAME=secutiry-app
ENV APP_HOME=/app

RUN mkdir $APP_HOME

WORKDIR $APP_HOME

COPY build/libs/securitydevices-0.0.1-SNAPSHOT.jar $APP_HOME/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
