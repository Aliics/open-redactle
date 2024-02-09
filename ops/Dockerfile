FROM eclipse-temurin:21.0.2_13-jre-jammy

ARG APP_JAR

COPY $APP_JAR /app/app.jar
WORKDIR /app

ENTRYPOINT java -cp app.jar openredactle.server.main
