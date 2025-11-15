plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.21.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("Main")
}

tasks.register<JavaExec>("runAggregator") {
    group = "application"
    mainClass.set("aggregators.ResultAggregator")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("runWorker") {
    group = "application"
    mainClass.set("workers.TextWorker")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("runProducer") {
    group = "application"
    mainClass.set("producers.TextProducer")
    classpath = sourceSets["main"].runtimeClasspath
    args("text.txt")
}