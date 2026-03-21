plugins {
    kotlin("plugin.spring") version "2.2.21"
}

val springBootVersion = "3.4.3"

dependencies {
    api(project(":valiktor-spring:valiktor-spring"))
    api(project(":valiktor-spring:valiktor-spring-boot-autoconfigure"))
    api("org.springframework.boot:spring-boot-starter:$springBootVersion")
}
