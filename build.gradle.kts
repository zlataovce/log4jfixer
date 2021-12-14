plugins {
    id("com.github.johnrengelman.shadow") version "7.1.0"
    kotlin("jvm") version "1.5.10"
}

group = "me.kcra"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("commons-cli:commons-cli:1.5.0")
    testImplementation(kotlin("test"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.kcra.log4jfixer.Log4JFixerKt"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}