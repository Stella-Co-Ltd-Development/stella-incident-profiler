plugins {
    `java-library`
}

dependencies {
    api(project(":app-core"))
    testImplementation(project(":app-mock"))
}
