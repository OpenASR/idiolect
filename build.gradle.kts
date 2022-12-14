import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
    idea apply true
    id("org.jetbrains.intellij") version "1.0"
    kotlin("jvm") version "1.5.20-M1"
    id("com.github.ben-manes.versions") version "0.39.0"
}

intellij {
    version.set("2021.1.1")
    pluginName.set("idear")
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("AceJump:3.6.2", "java"))
}

group = "org.openasr"
version = "1.3.4"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

//    named<Zip>("buildPlugin") {
//        dependsOn("test")
//        archiveFileName.set("idear.zip")
//    }

    withType<RunIdeTask> {
        dependsOn("test")
        findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
    }

//    withType<PublishTask> {
//        val intellijPublishToken: String? by project
//        token(intellijPublishToken)
//    }

    patchPluginXml {
        sinceBuild.set("201.6668.0")
    }
}

repositories {
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://alphacephei.com/maven/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.sourceforge.javaflacencoder:java-flac-encoder:0.3.7")
    implementation("edu.cmu.sphinx:sphinx4-core:5prealpha-SNAPSHOT")
    implementation("com.mashape.unirest:unirest-java:1.4.9")
    implementation("org.codehaus.jettison:jettison:1.4.1")
    implementation("de.dfki.mary:voice-cmu-slt-hsmm:5.2")
    implementation("com.amazonaws:aws-java-sdk-cognitoidentity:1.11.340")
    implementation("com.amazonaws:aws-java-sdk-lex:1.11.340")
    implementation("com.amazonaws:aws-java-sdk-polly:1.11.340")
    implementation("com.googlecode.soundlibs:jlayer:1.0.1.4")
    implementation("com.google.cloud:google-cloud-speech:0.32.0-alpha")
    implementation("com.alphacephei:vosk:0.3.21+")
    testImplementation("junit:junit:4.13")
}
