# Estágio de build: compila o jar com o Maven Wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q -DskipTests package

# Estágio final: só o JRE e o jar, rodando sem root
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 10001 app
USER app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
