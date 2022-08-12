plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.errorprone)
    implementation(libs.plugin.indra)
    implementation(libs.plugin.licenser)
}

dependencies {
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
