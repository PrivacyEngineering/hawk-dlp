plugins {
    kotlin("jvm")
}

group = "io.hawk"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0-rc2")
}
