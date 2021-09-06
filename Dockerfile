FROM gradle:6.9.1-jdk11-hotspot as BUILD_IMAGE
COPY --chown=gradle:gradle . /home/jpb/
WORKDIR /home/jpb/wss-server
RUN gradle bootJar

FROM openjdk:11.0.12-jre
WORKDIR /home/jpb
COPY --from=BUILD_IMAGE /home/jpb/wss-server/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
