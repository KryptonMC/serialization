enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.kryptonmc.org/releases")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven("https://repo.stellardrift.ca/repository/internal/") {
            name = "stellardriftReleases"
            mavenContent { releasesOnly() }
        }
        maven("https://repo.stellardrift.ca/repository/snapshots/") {
            name = "stellardriftSnapshots"
            mavenContent { snapshotsOnly() }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "serialization"

sequenceOf("core", "gson", "nbt").forEach {
    include("serialization-$it")
    project(":serialization-$it").projectDir = file(it)
}
