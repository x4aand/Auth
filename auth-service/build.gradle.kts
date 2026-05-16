plugins {
    id("java")
    id("org.springframework.boot") version "3.1.10"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.fitnes.auth"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    //  Spring MVC + встроенный Tomcat + REST контроллеры
    implementation("org.springframework.boot:spring-boot-starter-web")

    //  Spring Security (аутентификация, авторизация, фильтры)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Валидация DTO ( @Valid, @NotNull, @Email и т.д.)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Драйвер PostgreSQL (только во время запуска)
    runtimeOnly("org.postgresql:postgresql")

    //  Lombok (генерация геттеров, сеттеров, конструкторов)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // JWT библиотека от Auth0 (создание и проверка токенов)
    implementation("com.auth0:java-jwt:4.5.0")

    // Тестирование (JUnit, Mockito, Spring Test)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Log4j API (интерфейс логирования)
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")

    // Log4j Core (реализация логирования)
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")

    // JJWT API (ещё одна библиотека для JWT)
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")

    // JJWT реализация (нужна во время выполнения)
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")

    // JJWT поддержка Jackson (JSON сериализация)
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // SWAGER для проверки API
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    compileOnly("org.mapstruct:mapstruct:1.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    compileOnly("org.mapstruct:mapstruct:1.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")

    // Redis кэш БД
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Dev tools

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // миграция бд

    implementation("org.flywaydb:flyway-core")

    implementation("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}