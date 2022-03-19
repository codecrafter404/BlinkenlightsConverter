import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me._4o4"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.openpnp/opencv
    implementation("org.openpnp:opencv:4.5.1-2")
    // logging
    implementation("org.tinylog:tinylog-impl:2.4.1")
    implementation("org.tinylog:tinylog-api-kotlin:2.4.1")
    // cli
    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    // templating
    // https://mvnrepository.com/artifact/org.freemarker/freemarker
    implementation("org.freemarker:freemarker:2.3.31")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}