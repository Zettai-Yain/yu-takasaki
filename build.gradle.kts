import kotlin.collections.listOf

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    id("org.jetbrains.kotlin.kapt") version "1.4.31"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("io.micronaut.application") version "1.3.4"
}

version = "1.0.0"
group = "com.nanabell.nico.discord"

val kotlinVersion = project.properties["kotlinVersion"]
val jdaVersion = project.properties["jdaVersion"]
repositories {
    mavenCentral()
    jcenter()

    maven("https://repo.spring.io/libs-snapshot")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.nanabell.nico.takasaki.*")
    }
}

kapt {
    arguments {
        arg("micronaut.openapi.views.spec", "swagger-ui.enabled=true,swagger-ui.theme=flattop")
    }
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut.openapi:micronaut-openapi")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")

    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-http-client")
    implementation("javax.annotation:javax.annotation-api")
    implementation("io.swagger.core.v3:swagger-annotations")

    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-influx")
    constraints {
        implementation("io.micrometer:micrometer-core:1.7.0-M1")
        implementation("io.micrometer:micrometer-registry-influx:1.7.0-M1") {
            because("Required for Influx2.0 Support")
        }
    }

    implementation("net.dv8tion:JDA:${jdaVersion}") {
        exclude(null, "opus-java")
    }

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
}


application {
    mainClass.set("com.nanabell.nico.takasaki.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("14")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "14"
            useIR = true
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "14"
        }
    }

    jar {
        exclude("application-*.yml")
    }

    shadowJar {
        exclude("application-*.yml")
        mergeServiceFiles()
    }

    dockerBuild {
        dependsOn(shadowJar)

        dockerFile.set(file("${project.projectDir}/Dockerfile"))
        images.set(listOf(
            "registry.zettai-yain.dev/${project.name}:${project.version}",
            "registry.zettai-yain.dev/${project.name}"
        ))
    }
}
