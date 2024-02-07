FROM eclipse-temurin:21.0.2_13-jre-jammy

ARG APP_JAR

COPY $APP_JAR /app/app.jar
WORKDIR /app

# docker run open-redactle-server java -cp app.jar openredactle.server.main
# docker run open-redactle-scraper java -cp app.jar openredactle.scraper.main
