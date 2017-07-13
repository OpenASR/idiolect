buildscript {
  repositories {
    maven { setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service") }
  }
}

plugins {
  id("org.jetbrains.intellij") version "0.2.14"
  id("org.jetbrains.kotlin.jvm") version "1.1.3"
}

intellij {
  pluginName = "idear"
  updateSinceUntilBuild = false
  setPlugins("acejump:3.3.4")
}

group = "org.openasr"
version = "1.3"

repositories {
  mavenLocal()
  jcenter()
  maven {
    setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
  }
}

dependencies {
  compile("edu.cmu.sphinx:sphinx4-core:5prealpha-SNAPSHOT")
  compile("com.mashape.unirest:unirest-java:1.4.7")
  compile("org.codehaus.jettison:jettison:1.3.7")
  compile("de.dfki.mary:voice-cmu-slt-hsmm:5.2")
  compile("com.amazonaws:aws-java-sdk-lex:1.11.160")
  compile("com.amazonaws:aws-java-sdk-polly:1.11.160")
  compile("com.googlecode.soundlibs:jlayer:1.0.1-1")
  compile("com.google.cloud:google-cloud-speech:0.17.1-alpha")
  testCompile("junit:junit:4.12")

// TODO: Gradle failed to get from mavenLocal - weird errors, Should be able to restore java-speech-api and remove the rest
  compile("com.darkprograms.speech:java-speech-api:2.0.0-SNAPSHOT")
//  compile("net.sourceforge.javaflacencoders:java-flac-encoder:0.3.7")
}
