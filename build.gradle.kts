plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("jacoco")
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:4.3.2")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * AWS S3
     */
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.772")

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

    /**
     * Jacoco
     */
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

val jacocoInclude = listOf(
    "**/postservice/service/**",
    "**/postservice/validator/**",
    "**/postservice/filter/**",
    "**/postservice/controller/**"
)

jacoco {
    toolVersion = "0.8.7"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(file("${buildDir}/jacocoHtml"))
    }
    classDirectories.setFrom(
        fileTree(project.buildDir) {
            include(jacocoInclude)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "BUNDLE"
            enabled = true
            includes = jacocoInclude

            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.register("checkTotalCoverage") {
    dependsOn(tasks.jacocoTestReport)
    doLast {
        val reportFile = file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        val rule = tasks.jacocoTestCoverageVerification.get()
            .violationRules
            .rules.first()
            .limits.first()

        if (reportFile.exists()) {
            val minCoverage = rule.minimum?.toFloat()!!
            val counter = rule.counter

            val reportContent = reportFile.readText()
            val regex = Regex("""<counter type="$counter" missed="(\d+)" covered="(\d+)"""")
            val matches = regex.findAll(reportContent)

            var totalCovered = 0
            var totalMissed = 0

            for (match in matches) {
                val missed = match.groups[1]?.value?.toIntOrNull() ?: 0
                val covered = match.groups[2]?.value?.toIntOrNull() ?: 0

                totalMissed += missed
                totalCovered += covered
            }

            val total = totalCovered + totalMissed
            val coveragePercentage = (totalCovered.toFloat() / total.toFloat())

            println("Total test coverage: ${coveragePercentage * 100}%")
            if (coveragePercentage < minCoverage) {
                logger.warn("Warning: Total test coverage is below ${minCoverage * 100}%.")
            }
        } else {
            logger.error("Coverage report not found.")
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.build {
    finalizedBy("checkTotalCoverage")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}
