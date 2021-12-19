val koin_version: String by project
val ktorm_version: String by project
val dbcp_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgresql_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
}

group = "ru.itmo.mpi"
version = "0.0.1"
application {
    mainClass.set("ru.itmo.mpi.api.ApplicationKt")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.apache.commons:commons-dbcp2:$dbcp_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
