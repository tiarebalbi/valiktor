plugins {
    kotlin("plugin.spring") version "2.2.21"
}

val springVersion = "6.2.6"
val jacksonVersion = "2.19.4"
val servletVersion = "6.0.0"
val jsonAssertVersion = "1.5.0"
val xmlUnitVersion = "2.10.0"

dependencies {
    api(project(":valiktor-core"))
    compileOnly("org.springframework:spring-webmvc:$springVersion")
    compileOnly("org.springframework:spring-webflux:$springVersion")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    testImplementation("org.springframework:spring-webmvc:$springVersion")
    testImplementation("org.springframework:spring-webflux:$springVersion")
    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("jakarta.servlet:jakarta.servlet-api:$servletVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    testRuntimeOnly("org.skyscreamer:jsonassert:$jsonAssertVersion")
    testRuntimeOnly("org.xmlunit:xmlunit-core:$xmlUnitVersion")
}
