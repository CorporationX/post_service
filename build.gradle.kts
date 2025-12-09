plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
    checkstyle
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

    all {
        resolutionStrategy {
            force("com.fasterxml.jackson.core:jackson-databind:2.15.2")
            force("com.fasterxml.jackson.core:jackson-core:2.15.2")
            force("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
    implementation("org.springframework.retry:spring-retry:2.0.7")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core:4.25.0")
    implementation("redis.clients:jedis:5.1.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("org.fusesource.jansi:jansi:2.4.0")

    /**
     * Test containers
     */
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Test Lombok
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    /**
     * Для проверки Kafka messages
     */
    testImplementation("org.awaitility:awaitility:4.2.0")

    /**
     * Kafka
     */
    implementation("org.apache.kafka:kafka-clients:3.7.0")
}

tasks.withType<Test> {
    useJUnitPlatform()

    systemProperty("testcontainers.reuse.enable", "true")
    systemProperty("testcontainers.checks.disable", "true")
    systemProperty("testcontainers.startup.timeout", "120s")

    testLogging {
        events("passed", "skipped", "failed", "standard_out", "standard_error")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
    options.isFork = true
}

tasks.bootJar {
    archiveFileName.set("service.jar")
}