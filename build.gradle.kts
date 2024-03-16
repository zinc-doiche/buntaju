plugins {
    kotlin("jvm") version "1.9.22"
}

group = "zinc.doiche"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")
    implementation("org.mongodb:bson-kotlinx:4.11.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.21")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

configurations.implementation.configure {
    isCanBeResolved = true
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.implementation.get().map { if (it.isDirectory) it else zipTree(it) })

        manifest {
            attributes("Main-Class" to "zinc.doiche.MainKt")
        }
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    javadoc {
        options.encoding = "UTF-8"
    }
}

kotlin {
    jvmToolchain(17)
}