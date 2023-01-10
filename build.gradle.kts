import org.jetbrains.changelog.Changelog.OutputType.HTML

plugins {
  kotlin("jvm") version "1.8.0"
  id("org.jetbrains.intellij") version "1.11.0"
  id("com.github.ben-manes.versions") version "0.44.0"
  id("org.jetbrains.changelog") version "2.0.0"
}

group = "org.openasr"
version = "1.4.6-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

intellij {
  version.set("2022.3.1") // The version of the IntelliJ Platform IDE that will be used to build the plugin
  pluginName.set("idiolect")
  updateSinceUntilBuild.set(false)
  plugins.set(listOf("java"))
}

tasks {
  changelog {
    groups.set(listOf("Added", "Changed", "Removed", "Fixed"))
  }

  patchPluginXml {
    version.set("${project.version}")
    sinceBuild.set("222.*")
//    untilBuild.set("223.*")

    changeNotes.set(provider {
      changelog.renderItem(changelog.getAll().values.first(), HTML)
    })
  }

  val jvmTarget = "17"
  compileKotlin { kotlinOptions.jvmTarget = jvmTarget }

  compileTestKotlin {
    exclude(
      "**/windows/**",
      "**/mac/**",
      "**/ActionRecognizerManagerTest.kt"
    )
    kotlinOptions.jvmTarget = jvmTarget
  }

  buildPlugin {
    dependsOn("test")
    archiveFileName.set("idiolect.zip")
  }

  runIde {
    dependsOn("test")
    findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
  }

  if (System.getenv("GITHUB_REF_NAME") != null
      && !System.getenv("INTELLIJ_CERTIFICATE_CHAIN").isNullOrEmpty())
  {
    signPlugin {
      certificateChain.set(System.getenv("INTELLIJ_CERTIFICATE_CHAIN"))
      privateKey.set(System.getenv("INTELLIJ_PRIVATE_KEY"))
      password.set(System.getenv("INTELLIJ_PRIVATE_KEY_PASSWORD"))
      inputArchiveFile.set(File("./build/distributions/idiolect.zip"))
      outputArchiveFile.set(File("./build/distributions/idiolect-signed.zip"))
    }

    publishPlugin {
      distributionFile.set(File("./build/distributions/idiolect-signed.zip"))
      if (System.getenv("GITHUB_REF_NAME") != "master") {
        // Users can configure a new custom plugin repository: https://plugins.jetbrains.com/plugins/eap/list
        // https://www.jetbrains.com/help/idea/managing-plugins.html#repos
        // alpha/beta/eap/canary
        channels.set(listOf(System.getenv("INTELLIJ_CHANNEL")))
        // ...could also add updatePlugins.xml to github site
        // https://plugins.jetbrains.com/docs/intellij/custom-plugin-repository.html#describing-your-plugins-in-updatepluginsxml-file
      }
      token.set(System.getenv("INTELLIJ_PUBLISH_TOKEN"))
    }
  }
}

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://mlt.jfrog.io/artifactory/mlt-mvn-releases-local")
}

dependencies {
  implementation("net.java.dev.jna:jna:5.12.1")
  implementation("com.alphacephei:vosk:0.3.45")
  implementation("io.github.jonelo:jAdapterForNativeTTS:0.9.9")
  testImplementation("org.reflections:reflections:0.10.2")
}
