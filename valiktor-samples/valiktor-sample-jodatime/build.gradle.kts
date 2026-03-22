val jodaTimeVersion = "2.10.6"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":valiktor-core"))
    implementation(project(":valiktor-jodatime"))
    implementation("joda-time:joda-time:$jodaTimeVersion")

    testImplementation(project(":valiktor-test"))
}
