val jodaMoneyVersion = "1.0.1"

dependencies {
    api(project(":valiktor-core"))
    compileOnly("org.joda:joda-money:$jodaMoneyVersion")

    testImplementation(kotlin("reflect"))
    testImplementation("org.joda:joda-money:$jodaMoneyVersion")
}
