plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
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
    // 1. gRPC 핵심 라이브러리 (Client 역할)
    implementation("io.grpc:grpc-netty-shaded:1.80.0")
    implementation("io.grpc:grpc-protobuf:1.80.0")
    runtimeOnly("io.grpc:grpc-kotlin-stub:1.5.0")
    // 2. 프로토콜 버퍼 빌드 도구 (gRPC 코드 생성을 위해 필요) [cite: 16]
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // 3. Redis 연동 (실시간 차단 목록 조회용) [cite: 21]
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 4. SDK가 Spring 환경에서 돌아갈 것임을 가정 (Compile 타임에만 참조)
    compileOnly("org.springframework.boot:spring-boot-starter-web")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
