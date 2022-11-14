plugins {
    kotlin("jvm")
}

group = "io.hawk"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.slf4j:slf4j-api:2.0.3")
}
