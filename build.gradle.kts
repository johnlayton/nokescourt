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
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
//    implementation(libs.spring.boot.starter.mustache)
//    implementation(libs.spring.boot.starter.handlebars)
    implementation(libs.handlebars)
    implementation(libs.handlebars.guava.cache)
    implementation(libs.handlebars.helpers)
    implementation(libs.handlebars.humanize)
    implementation(libs.handlebars.markdown)
    implementation(libs.handlebars.jackson2)
    implementation(libs.joda.time)
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.itext.core)
    implementation(libs.itext.html)
    implementation(libs.logstash.logback.encoder)
}
