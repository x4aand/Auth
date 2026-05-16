# Stage 1: build
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar -x test --no-daemon

# Stage 2: runtime
FROM bellsoft/liberica-openjre-alpine:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]