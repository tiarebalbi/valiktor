plugins {
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.4.3"
}

val jacksonMoneyVersion = "1.2.0"
val moneyVersion = "1.1"
val monetaVersion = "1.4.2"

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":valiktor-javamoney"))
    implementation(project(":valiktor-javatime"))
    implementation(project(":valiktor-spring:valiktor-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("org.zalando:jackson-datatype-money:$jacksonMoneyVersion")
    implementation("javax.money:money-api:$moneyVersion")
    runtimeOnly("org.javamoney:moneta:$monetaVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
