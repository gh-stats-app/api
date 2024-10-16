plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.adarshr.test-logger") version "4.0.0"
    id("net.ltgt.errorprone") version "3.1.0"
}

group = "app.gh-stats"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    errorprone("com.google.errorprone:error_prone_core:2.18.0")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.micrometer:micrometer-registry-prometheus:latest.release")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.github.slugify:slugify:3.0.6")
    implementation("org.ocpsoft.prettytime:prettytime:5.0.7.Final")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.flywaydb:flyway-mysql")

    runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.4")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.tngtech.archunit:archunit:1.1.0")
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
