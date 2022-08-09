import java.net.URI

plugins {
    id("org.cadixdev.licenser")
    `java-library`
    `maven-publish`
    signing
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

license {
    header(project.rootProject.resources.text.fromFile("HEADER.txt"))
    newLine(false)
}

val sourceSets = extensions.getByName("sourceSets") as SourceSetContainer

task<Jar>("sourcesJar") {
    from(sourceSets.named("main").get().java)
    archiveClassifier.set("sources")
}

task<Jar>("javadocJar") {
    from(tasks["javadoc"])
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            val releases = URI("https://repo.kryptonmc.org/releases")
            val snapshots = URI("https://repo.kryptonmc.org/snapshots")
            url = if (project.version.toString().endsWith("SNAPSHOT")) snapshots else releases
            credentials(PasswordCredentials::class)
        }
    }
    publications.create<MavenPublication>("mavenJava") {
        groupId = rootProject.group as String
        artifactId = project.name
        version = rootProject.version as String

        from(components["java"])
        artifact(tasks["sourcesJar"])
        artifact(tasks["javadocJar"])

        pom {
            name.set("Krypton Serialization")
            description.set("A library for generic serialization of generic data.")
            url.set("https://www.kryptonmc.org")
            inceptionYear.set("2021")
            packaging = "jar"

            developers {
                developer("bombardygamer", "Callum Seabrook", "callum.seabrook@prevarinite.com", "Europe/London", "Developer", "Maintainer")
            }

            organization {
                name.set("KryptonMC")
                url.set("https://www.kryptonmc.org")
            }

            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/KryptonMC/serialization/issues")
            }

            scm {
                connection.set("scm:git:git://github.com/KryptonMC/serialization.git")
                developerConnection.set("scm:git:ssh://github.com:KryptonMC/serialization.git")
                url.set("https://github.com/KryptonMC/serialization")
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

tasks["build"].dependsOn(tasks["test"])

