import com.google.protobuf.gradle.id
import org.jetbrains.changelog.Changelog.OutputType.HTML

val grpcVersion = "1.43.1"
val protobufVersion = "3.19.4"
val grpcKotlinVersion = "1.3.0"

plugins {
  kotlin("jvm") version "1.8.20"
  id("org.jetbrains.intellij") version "1.13.3"
  id("com.github.ben-manes.versions") version "0.46.0"
  id("org.jetbrains.changelog") version "2.0.0"
  id("com.google.protobuf") version "0.9.3"
}

group = "org.openasr"
version = "1.4.9-SNAPSHOT"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

intellij {
  version = "2023.1" // The version of the IntelliJ Platform IDE that will be used to build the plugin
  pluginName = "idiolect"
  updateSinceUntilBuild = false
  plugins = listOf("java")
}


protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${protobufVersion}"
  }
  plugins {
    create("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
    }
    create("grpckt") {
      artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpcKotlinVersion}:jdk8@jar"
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


tasks {
  changelog {
    groups = listOf("Added", "Changed", "Removed", "Fixed")
  }

  patchPluginXml {
    version = "${project.version}"
    sinceBuild = "223"
//    untilBuild = "223.*")

    changeNotes = provider {
      changelog.renderItem(changelog.getAll().values.first(), HTML)
    }
  }


  val jvmTarget = "17"
  compileKotlin { kotlinOptions.jvmTarget = jvmTarget }

  // New test task without mac tests for runIde
  val quickTests by registering(Test::class) {
    exclude(
      "**/windows/**",
      "**/mac/**",
      "**/ActionRecognizerManagerTest.kt"
    )
  }

  compileTestKotlin {
    val osName = System.getProperty("os.name").lowercase()
    exclude(
      if ("windows" in osName) "" else "**/windows/**",
      if ("mac" in osName) "" else "**/mac/**",
      "**/ActionRecognizerManagerTest.kt"
    )
    kotlinOptions.jvmTarget = jvmTarget
  }

  test {
    testLogging {
      outputs.upToDateWhen { false }
      showStandardStreams = true
    }
  }

  buildPlugin {
    dependsOn("quickTests")
    archiveFileName = "idiolect.zip"
  }

  runIde {
    // depend on test but exclude the tests that contain mac
    dependsOn("quickTests")
    findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
    systemProperty("idiolect.environment", "development")
  }

  if (System.getenv("GITHUB_REF_NAME") != null
      && !System.getenv("INTELLIJ_CERTIFICATE_CHAIN").isNullOrEmpty()) {
    signPlugin {
      certificateChain = System.getenv("INTELLIJ_CERTIFICATE_CHAIN")
      privateKey = System.getenv("INTELLIJ_PRIVATE_KEY")
      password = System.getenv("INTELLIJ_PRIVATE_KEY_PASSWORD")
      inputArchiveFile = File("./build/distributions/idiolect.zip")
      outputArchiveFile = File("./build/distributions/idiolect-signed.zip")
    }

    publishPlugin {
      distributionFile = File("./build/distributions/idiolect-signed.zip")
      if (System.getenv("GITHUB_REF_NAME") != "master") {
        // Users can configure a new custom plugin repository: https://plugins.jetbrains.com/plugins/eap/list
        // https://www.jetbrains.com/help/idea/managing-plugins.html#repos
        // alpha/beta/eap/canary
        channels = listOf(System.getenv("INTELLIJ_CHANNEL"))
        // ...could also add updatePlugins.xml to github site
        // https://plugins.jetbrains.com/docs/intellij/custom-plugin-repository.html#describing-your-plugins-in-updatepluginsxml-file
      }
      token = System.getenv("INTELLIJ_PUBLISH_TOKEN")
    }
  }
}
//
//tasks.register<GenerateProtoKotlin>("generateProtoKotlin") {
////  classpath = configurations. compileClasspath
////  mainClass.set("com.google.protobuf.compiler.Protoc")
////  args = listOf("-I", "src/main/proto", "-o", "build/generated/source/proto", "whisper_server.proto")
//
//}

repositories {
  mavenLocal()
  mavenCentral()
}


dependencies {
  implementation("net.java.dev.jna:jna:5.13.0")
  implementation("com.alphacephei:vosk:0.3.45")
  implementation("io.github.jonelo:jAdapterForNativeTTS:0.9.9")

  // gRPC for Whisper ASR
  compileOnly("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
  compileOnly("io.grpc:grpc-protobuf:$grpcVersion")
  compileOnly("io.grpc:grpc-netty:$grpcVersion")
  compileOnly("com.google.protobuf:protobuf-java:$protobufVersion")
  compileOnly("com.google.protobuf:protobuf-java-util:$protobufVersion")
  compileOnly("com.google.protobuf:protobuf-kotlin:$protobufVersion")

  implementation("com.theokanning.openai-gpt3-java:service:0.12.0")
//  implementation("com.aallam.openai:openai-client:3.2.3")  // thto
  testImplementation("org.reflections:reflections:0.10.2")
  testImplementation("ai.hypergraph:kaliningraph:0.2.1") {
    exclude(group = "org.sosy-lab")
  }
  testImplementation("io.mockk:mockk:1.13.5")
}
