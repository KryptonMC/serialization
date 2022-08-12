import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.publishing")
    id("net.kyori.indra.license-header")
    id("net.ltgt.errorprone")
    jacoco
}

// expose version catalogue
val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
    errorprone(libs.errorprone)
    annotationProcessor(libs.contractValidator)
    checkstyle(libs.stylecheck)
}

indra {
    javaVersions {
        target(17)
    }
    checkstyle(libs.versions.checkstyle.get())

    github("KryptonMC", "serialization")
    mitLicense()

    publishReleasesTo("krypton-repo", "https://repo.kryptonmc.org/releases")
    publishSnapshotsTo("krypton-repo", "https://repo.kryptonmc.org/snapshots")
    configurePublications {
        pom {
            developers {
                developer("bombardygamer", "Callum Seabrook", "callum.seabrook@prevarinite.com", "Europe/London", "Developer", "Maintainer")
            }
        }
    }
}

tasks {
    jacocoTestReport {
        dependsOn(test)
    }
    withType<JavaCompile> {
        options.errorprone {
            disable("InvalidParam") // Unfortunately, Error Prone doesn't understand Record parameter JavaDocs yet.
            disable("TypeNameShadowing") // This is fine.
        }
    }
}

tasks["build"].dependsOn(tasks["test"])

