rootProject.name = "serialization-build-logic"

dependencyResolutionManagement {
    repositories {
        maven("https://repo.stellardrift.ca/repository/internal/") {
            name = "stellardriftReleases"
            mavenContent { releasesOnly() }
        }
        maven("https://repo.stellardrift.ca/repository/snapshots/") {
            name = "stellardriftSnapshots"
            mavenContent { snapshotsOnly() }
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml")) // include from parent project
        }
    }
}
