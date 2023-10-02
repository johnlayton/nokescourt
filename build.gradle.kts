@Suppress(
        "DSL_SCOPE_VIOLATION",
        "MISSING_DEPENDENCY_CLASS",
        "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
        "FUNCTION_CALL_EXPECTED"
)
plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    maven("https://repo.spring.io/release")
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
    maven("https://packages.confluent.io/maven/")
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.mustache)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.itext)
    implementation(libs.logstash.logback.encoder)
}
