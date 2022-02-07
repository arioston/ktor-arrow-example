import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") plugins {
  application
  alias(libs.plugins.kotest.multiplatform)
  id(libs.plugins.kotlin.jvm.pluginId)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.dokka)
  id(libs.plugins.detekt.pluginId)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqldelight)
}

application {
  mainClass by "io.github.nomisrev.ApplicationKt"
}

sqldelight {
  database("SqlDelight") {
    packageName = "io.github.nomisrev"
    dialect = "postgresql"
  }
}

allprojects {
  extra.set("dokka.outputDirectory", rootDir.resolve("docs"))
  setupDetekt()
}

repositories {
  mavenCentral()
  maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
    }
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
  }

  test {
    useJUnitPlatform()
    extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
      includes = listOf("io.github.nomisrev.*")
    }
  }
}

dependencies {
  implementation(libs.arrow.core)
  implementation(libs.arrow.fx)

  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.server.netty)
  implementation(libs.ktor.serialization)
  implementation(libs.logback.classic)

  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.squareup.sqldelight:jdbc-driver:1.5.3")
  implementation("org.postgresql:postgresql:42.2.20")

  testImplementation(libs.ktor.server.tests)
  testImplementation(libs.kotest.runnerJUnit5)
  testImplementation(libs.kotest.frameworkEngine)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.property)
}
