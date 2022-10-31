pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}
rootProject.name = "hawk-dlp"
include("hawk-dlp-common")
include("hawk-dlp-integration")
include("hawk-dlp-integration-google-cloud-dlp")
include("hawk-dlp-integration-amazon-macie2")
