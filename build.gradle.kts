plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("checkstyle")
    id("org.jsonschema2pojo") version "1.2.1"
    id("jacoco")
    kotlin("jvm")
}

group = "faang.school"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springframework.retry:spring-retry:2.0.2")
    implementation("org.springframework:spring-aspects")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.5.0")
    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * AWS S3
     */
    implementation(platform("software.amazon.awssdk:bom:2.21.1"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:netty-nio-client")

    /**
     * Image processing
     */
    implementation("net.coobird:thumbnailator:0.4.19")

    /**
     * Multipart file upload
     */
    implementation("commons-fileupload:commons-fileupload:1.4")
    implementation("commons-io:commons-io:2.11.0")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    implementation(kotlin("stdlib-jdk8"))
}

configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
    reportsDir = file("$buildDir/reports/jacoco")
}


jsonSchema2Pojo {
    setSource(files("src/main/resources/json"))
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = "com.json.student"
    setSourceType("jsonschema")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    configProperties = mapOf(
        "checkstyle.enableExternalDtdLoad" to "true"
    )
}

tasks.checkstyleMain {
    source = fileTree("${project.rootDir}/src/main/java")
    include("**/*.java")
    exclude("**/resources/**")

    classpath = files()
}

tasks.checkstyleTest {
    source = fileTree("${project.rootDir}/src/test")
    include("**/*.java")

    classpath = files()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}


val jacocoClassExclude = listOf(
    "com.json.student.*",
    "faang.school.postservice.client.*",
    "faang.school.postservice.config.*",
    "faang.school.postservice.model.*",
    "faang.school.postservice.dto.*",
    "faang.school.postservice.mapper.*",
    "faang.school.postservice.PostServiceApp",
    "faang.school.postservice.repository.*",
    "faang.school.postservice.utils.*",
    "faang.school.postservice.service.S3Service",
    "faang.school.postservice.controller.*",
    "faang.school.postservice.exception.*",
    "faang.school.postservice.scheduled",
    "faang.school.postservice.publisher.*",
    "faang.school.postservice.scheduler.ThreadPoolConfig"

)

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            isEnabled = true
            excludes = jacocoClassExclude
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.7".toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
    }

    classDirectories.setFrom(
        classDirectories.files.map { dir ->
            fileTree(dir) {
                exclude(
                    "com/json/student/**",
                    "faang/school/postservice/client/**",
                    "faang/school/postservice/config/**",
                    "faang/school/postservice/model/**",
                    "faang/school/postservice/dto/**",
                    "faang/school/postservice/mapper/**",
                    "faang/school/postservice/PostServiceApp*",
                    "faang/school/postservice/repository/**",
                    "faang/school/postservice/utils/**",
                    "faang/school/postservice/service/S3Service*",
                    "faang/school/postservice/controller/**",
                    "faang/school/postservice/exception/**",
                    "faang/school/postservice/scheduled/**",
                    "faang/school/postservice/publisher/**",
                    "faang/school/postservice/scheduler/ThreadPoolConfig*"
                )
            }
        }
    )
}
kotlin {
    jvmToolchain(17)
}