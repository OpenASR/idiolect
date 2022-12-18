import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
  idea apply true
  id("java")
  id("org.jetbrains.intellij") version "1.11.0"
  id("org.jetbrains.kotlin.jvm") version "1.8.0-RC"
  id("com.github.ben-manes.versions") version "0.44.0"
}

group = "org.openasr"
version = "1.3.5"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

intellij {
  version.set("2022.3")
  pluginName.set("idear")
  updateSinceUntilBuild.set(false)
  plugins.set(listOf("AceJump:3.6.2", "java"))
}

tasks {
  patchPluginXml {
    version.set("${project.version}")
    sinceBuild.set("213")
    untilBuild.set("223.*")
  }

  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  compileTestKotlin {
    exclude("**/windows/**")
    kotlinOptions.jvmTarget = "17"
  }

  named<Zip>("buildPlugin") {
    dependsOn("test")
    archiveFileName.set("idear.zip")
  }

  runIde {
    dependsOn("test")
    findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
  }

//    withType<PublishTask> {
//        val intellijPublishToken: String? by project
//        token(intellijPublishToken)
//    }
}

repositories {
  mavenCentral()
  maven("https://mlt.jfrog.io/artifactory/mlt-mvn-releases-local")
  maven("https://oss.sonatype.org/content/repositories/releases/")
  maven("https://oss.sonatype.org/content/repositories/snapshots/")
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
