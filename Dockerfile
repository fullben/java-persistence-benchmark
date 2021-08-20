FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
COPY server/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
