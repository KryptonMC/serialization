enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        maven("https://repo.kryptonmc.org/releases")
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "serialization"

sequenceOf("core", "gson", "nbt").forEach {
    include("serialization-$it")
    project(":serialization-$it").projectDir = file(it)
}
