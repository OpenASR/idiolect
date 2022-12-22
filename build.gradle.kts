import org.jetbrains.changelog.Changelog.OutputType.HTML

plugins {
  kotlin("jvm") version "1.8.0-RC2"
  id("org.jetbrains.intellij") version "1.11.0"
  id("com.github.ben-manes.versions") version "0.44.0"
  id("org.jetbrains.changelog") version "2.0.0"
}

group = "org.openasr"
version = "1.4.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

intellij {
  version.set("2022.3")
  pluginName.set("idear")
  updateSinceUntilBuild.set(false)
  plugins.set(listOf("AceJump:3.6.2", "java"))
}

tasks {
  patchPluginXml {
    version.set("${project.version}")
    sinceBuild.set("222.*")
    untilBuild.set("223.*")

    changeNotes.set(provider {
      changelog.renderItem(changelog.getAll().values.first(), HTML)
    })
  }

  val jvmTarget = "17"

  compileKotlin { kotlinOptions.jvmTarget = jvmTarget }

  compileTestKotlin {
    exclude("**/windows/**")
    kotlinOptions.jvmTarget = jvmTarget
  }

  buildPlugin {
    dependsOn("test")
    archiveFileName.set("idear.zip")
  }

  runIde {
    dependsOn("test")
    findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
  }

  signPlugin {
    certificateChain.set(System.getenv("INTELLIJ_CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("INTELLIJ_PRIVATE_KEY"))
    password.set(System.getenv("INTELLIJ_PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("INTELLIJ_PUBLISH_TOKEN"))
  }
}

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://mlt.jfrog.io/artifactory/mlt-mvn-releases-local")
}

dependencies {
  implementation("de.dfki.mary:voice-cmu-slt-hsmm:5.2.1") {
    exclude("com.twmacinta", "fast-md5")
    exclude("gov.nist.math", "Jampack")
  }
  implementation("net.java.dev.jna:jna:5.12.1")
  implementation("com.alphacephei:vosk:0.3.38")
  implementation("com.jsoniter:jsoniter:0.9.23")
  testImplementation("junit:junit:4.13.2")
}