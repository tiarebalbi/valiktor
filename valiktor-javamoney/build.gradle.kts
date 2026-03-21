val moneyVersion = "1.0.4"
val monetaVersion = "1.4.1"

dependencies {
    implementation(project(":valiktor-core"))
    compileOnly("javax.money:money-api-bp:$moneyVersion")

    testImplementation(kotlin("reflect"))
    testImplementation("javax.money:money-api-bp:$moneyVersion")
    testRuntimeOnly("org.javamoney:moneta-bp:$monetaVersion")
}
