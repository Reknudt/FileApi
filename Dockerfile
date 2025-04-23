FROM eclipse-temurin:17-jdk
WORKDIR /DPspring
COPY target/FileApi-1.0-SNAPSHOT.jar FileApi-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "FileApi-1.0-SNAPSHOT.jar"]