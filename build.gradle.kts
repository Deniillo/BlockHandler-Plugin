group = "ru.joutak"
version = System.getProperty("version")
val commitHash = System.getProperty("commitHash")
if (commitHash.isNotBlank()) {
    version = "$version-$commitHash"
}

val targetJavaVersion = 21
plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
}


kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    register("deployAndReload", Copy::class) {
        dependsOn("shadowJar")

        //val serverDir = File(System.getenv("SERVER_PATH") ?: error("SERVER_PATH не установлен!"))
        //val pluginsDir = File(serverDir, "plugins")

        from(shadowJar.get().archiveFile)
        into("server/plugins")

        doLast {
            exec {
                commandLine(
                    "docker", "exec", "local-server",
                    "rcon-cli", "reload", "confirm"
                )
            }
            println("Плагин обновлен. Сервер перезагружен через RCON")
        }
    }
}

tasks.named("build") {
    dependsOn("shadowJar")
    finalizedBy("deployAndReload")
}

tasks.shadowJar {
    archiveClassifier = ""
    archiveFileName.set("${project.name}.jar")

    //minimize()
    //configurations = listOf(project.configurations.runtimeClasspath.get())

    //relocate("kotlin", "ru.joutak.shadow.kotlin") {
    //    exclude("kotlin.Metadata")
    //}
}

tasks.jar {
    finalizedBy("shadowJar")
    enabled = false
}

tasks.processResources {
    val props = mapOf("version" to version, "pluginName" to project.name)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}