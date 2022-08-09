import org.gradle.api.publish.maven.MavenPomDeveloperSpec

fun MavenPomDeveloperSpec.developer(id: String, name: String, email: String, timezone: String, vararg roles: String) {
    developer {
        this.id.set(id)
        this.name.set(name)
        this.email.set(email)
        this.timezone.set(timezone)
        organization.set("KryptonMC")
        organizationUrl.set("https://www.kryptonmc.org")
        this.roles.set(roles.toSet())
    }
}
