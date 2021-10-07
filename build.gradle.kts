plugins {
    java
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.adarshr.test-logger") version "3.0.0"
}

group = "app.gh-stats"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.micrometer:micrometer-core:1.7.3")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("com.github.slugify:slugify:2.5")
    implementation("org.ocpsoft.prettytime:prettytime:5.0.2.Final")
    implementation("io.micrometer:micrometer-registry-graphite:latest.release")

    runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.mariadb:r2dbc-mariadb:1.0.3")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    testImplementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}