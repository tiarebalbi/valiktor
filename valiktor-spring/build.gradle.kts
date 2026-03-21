tasks {
    withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
        enabled = false
    }

    named("publishMavenJavaPublicationToMavenLocal") {
        enabled = false
    }

    named("publishMavenJavaPublicationToMavenRepository") {
        enabled = false
    }
}
