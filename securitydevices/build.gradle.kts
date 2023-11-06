plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.5")

    implementation("org.springframework.kafka:spring-kafka:3.0.12")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("io.confluent:kafka-schema-registry-maven-plugin:7.5.1")
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.nats:jnats:2.16.14")

    implementation(project(":internal-api"))
    implementation(project(":securitydevices:core"))
    implementation(project(":securitydevices:user"))
    implementation(project(":securitydevices:device"))
    implementation(project(":securitydevices:device-status"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test:3.5.11")
    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testImplementation("io.mockk:mockk:1.13.8")
}

subprojects {

    apply(plugin = "kotlin")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {

        implementation(project(":"))
        implementation(project(":internal-api"))

        implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
        implementation("org.springframework.boot:spring-boot-starter-validation:3.1.5")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.5")
        implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.5")

        implementation("org.springframework.kafka:spring-kafka:3.0.12")
        implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
        implementation("io.confluent:kafka-schema-registry-maven-plugin:7.5.1")
        implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

        implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
        implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.nats:jnats:2.16.14")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test:3.5.11")
        testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }

    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
        enabled = false
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
