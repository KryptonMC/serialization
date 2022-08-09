plugins {
    id("serialization.common")
}

dependencies {
    api(projects.serializationCore)
    api(libs.gson)
}
