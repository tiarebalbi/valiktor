dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":valiktor-core"))
    implementation(project(":valiktor-javatime"))

    testImplementation(project(":valiktor-test"))
}
