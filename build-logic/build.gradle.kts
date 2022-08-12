plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.errorprone)
    implementation(libs.plugin.indra)
}

dependencies {
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
