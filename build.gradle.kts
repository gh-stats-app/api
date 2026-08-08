plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.errorprone)
}

group = "app.gh-stats"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

dependencies {
    errorprone(libs.errorprone.core)

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.data.r2dbc)
    implementation("org.springframework.boot:spring-boot-starter-jackson")
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.guava)
    implementation(libs.slugify)
    implementation(libs.prettytime)
    implementation(libs.emoji.java)
    implementation("org.wickedsource:diffparser:1.0")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation(libs.flyway.database.postgresql)
    implementation("com.github.bgalek.github:github-models-dotcom:0.0.25")

    runtimeOnly(libs.spring.boot.starter.jdbc)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.r2dbc.postgresql)
    runtimeOnly(libs.r2dbc.pool)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
    testImplementation(libs.archunit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.r2dbc)
    testImplementation(libs.testcontainers.junit.jupiter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    enabled = false
}
