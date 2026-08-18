import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "11.0.5"
  kotlin("plugin.spring") version "2.4.10"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:3.0.0")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.11.0")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:3.0.0")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.wiremock:wiremock-standalone:3.13.2")
  testImplementation("io.mockk:mockk:1.14.11")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.46") {
    exclude(group = "io.swagger.core.v3")
  }
}

kotlin {
  jvmToolchain(25)
  compilerOptions {
    freeCompilerArgs.addAll("-Xannotation-default-target=param-property")
  }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_25
  }
}
