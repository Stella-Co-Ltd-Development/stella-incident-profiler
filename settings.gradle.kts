pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "stella-incident-profiler"

include(
    "app-core",
    "app-mock",
    "app-aws",
    "app-jfr",
    "app-mcp",
    "app-storage",
    "app-test-support",
    "app-desktop",
)
