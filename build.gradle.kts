plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.6"
    id("maven-publish")
}



group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "pickstranger"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.grpc:grpc-netty-shaded:1.80.0")
    implementation("io.grpc:grpc-protobuf:1.80.0")
    implementation("io.grpc:grpc-stub:1.80.0")
    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")
    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    // Source: https://mvnrepository.com/artifact/com.google.protobuf/protobuf-kotlin
    implementation("com.google.protobuf:protobuf-kotlin:4.34.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.35.0-RC2"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.80.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    exclude("**/*.proto")
}
