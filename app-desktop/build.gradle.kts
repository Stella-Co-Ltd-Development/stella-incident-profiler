plugins {
    application
    alias(libs.plugins.javafx)
}

dependencies {
    implementation(project(":app-core"))
    implementation(project(":app-mock"))
    implementation(project(":app-mcp"))
    implementation(libs.jackson.databind)
}

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    mainClass.set("com.stella.incidentprofiler.desktop.StellaIncidentProfilerApp")
}

sourceSets {
    main {
        resources {
            srcDir(rootProject.projectDir)
            include("ui/ja-JP.json")
        }
    }
}
