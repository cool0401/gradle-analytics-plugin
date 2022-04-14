import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginId: String by project
val pluginDisplayName: String by project
val pluginDescription: String by project
val pluginImplementationClass: String by project
val pluginDeclarationName: String by project
val projectGroup: String by project
val projectVersion: String by project

plugins {
    kotlin("jvm") version "1.6.10"
    id("java-gradle-plugin")
    id("io.gitlab.arturbosch.detekt").version("1.20.0-RC2")
    id("org.gradle.jacoco")
    id("maven-publish")
}

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

plugins.withType<JavaPlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }
    }
}

gradlePlugin {
    plugins {
        create(pluginDeclarationName) {
            id = pluginId
            displayName = pluginDisplayName
            description = pluginDescription
            implementationClass = pluginImplementationClass
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.register("publishToLocal") {
    doLast {
        exec {
            commandLine(
                "./gradlew",
                "detekt",
                "build",
                "test",
                "publishToMavenLocal"
            ).args("--info", "--stacktrace")
        }
    }
}
