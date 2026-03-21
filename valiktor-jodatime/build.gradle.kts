val jodaTimeVersion = "2.10.6"

dependencies {
    implementation(project(":valiktor-core"))
    compileOnly("joda-time:joda-time:$jodaTimeVersion")

    testImplementation(kotlin("reflect"))
    testImplementation("joda-time:joda-time:$jodaTimeVersion")
}
