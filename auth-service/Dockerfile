# Stage 1: build
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /home/gradle/project

COPY . .

RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: runtime
FROM bellsoft/liberica-openjre-alpine:17

WORKDIR /application

RUN adduser -S spring-user
USER spring-user

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]