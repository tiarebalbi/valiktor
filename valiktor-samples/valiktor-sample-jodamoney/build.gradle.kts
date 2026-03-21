val jodaMoneyVersion = "1.0.1"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":valiktor-core"))
    implementation(project(":valiktor-jodamoney"))
    implementation("org.joda:joda-money:$jodaMoneyVersion")

    testImplementation(project(":valiktor-test"))
}
