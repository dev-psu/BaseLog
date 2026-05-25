plugins {
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
}
