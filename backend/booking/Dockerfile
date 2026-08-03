FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw -B -ntp dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -ntp clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S ironbus \
    && adduser -S ironbus -G ironbus

WORKDIR /app

COPY --from=build \
    /workspace/target/*.jar \
    /app/application.jar

USER ironbus

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-Djava.security.egd=file:/dev/urandom", "-jar", "/app/application.jar"]