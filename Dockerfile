FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

RUN apt-get update \
  && apt-get install -y --no-install-recommends maven \
  && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests clean package

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN useradd -r -u 10001 appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=20"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
