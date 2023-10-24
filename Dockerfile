#
# Build stage
#
FROM maven:3.6.3-openjdk-17 AS build
COPY app /home/cowbot/app
COPY bot-api /home/cowbot/bot-api
COPY config /home/cowbot/config
COPY contract /home/cowbot/contract
COPY db /home/cowbot/db
COPY rest-api /home/cowbot/rest-api
COPY schedule-api /home/cowbot/schedule-api
COPY service /home/cowbot/service
COPY util /home/cowbot/util
COPY pom.xml /home/cowbot
RUN mvn -f /home/cowbot/pom.xml clean -DskipTests=true package

#
# Package stage
#
FROM openjdk:17
COPY --from=build /home/cowbot/app/target/app-0.0.1-SNAPSHOT.jar /usr/local/lib/cowbot.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/cowbot.jar"]