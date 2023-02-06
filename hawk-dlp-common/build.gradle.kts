plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "io.hawk"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.slf4j:slf4j-api:2.0.5")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/PrivacyEngineering/hawk-dlp")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    // gradle hawk-dlp-common:publish
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
