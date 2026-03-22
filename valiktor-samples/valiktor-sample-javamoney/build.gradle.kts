val moneyVersion = "1.1"
val monetaVersion = "1.4.2"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":valiktor-core"))
    implementation(project(":valiktor-javamoney"))
    implementation("javax.money:money-api:$moneyVersion")

    testImplementation(project(":valiktor-test"))
    testRuntimeOnly("org.javamoney:moneta:$monetaVersion")
}
