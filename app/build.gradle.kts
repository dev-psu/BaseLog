plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":member"))
    implementation(project(":game"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.flyway.core)
    implementation(libs.flyway.mysql)
    implementation(libs.spring.boot.flyway)
    runtimeOnly(libs.h2)
    runtimeOnly(libs.mysql.connector.j)
    testImplementation(libs.spring.boot.starter.test)
}
