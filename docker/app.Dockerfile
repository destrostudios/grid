FROM eclipse-temurin:17-jre-alpine
WORKDIR /home
COPY target/grid-server-0.0.1-jar-with-dependencies.jar ./
ENTRYPOINT ["java", "-jar", "grid-server-0.0.1-jar-with-dependencies.jar"]