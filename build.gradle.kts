import org.gradle.script.lang.kotlin.*
import org.jetbrains.intellij.IntelliJPluginExtension

buildscript {
  repositories {
    gradleScriptKotlin()
    maven { setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service") }
  }

  dependencies {
    classpath(kotlinModule("gradle-plugin"))
  }
}

plugins {
  id("org.jetbrains.intellij") version "0.2.5"
}

apply {
  plugin("org.jetbrains.intellij")
  plugin("kotlin")
}

configure<IntelliJPluginExtension> {
  pluginName = "AceJump"
  updateSinceUntilBuild = false
}

group = "com.jetbrains"
version = "1.3"

repositories {
  mavenCentral()
  maven {
    setUrl("https://oss.jfrog.org/artifactory/repo/")
  }
  maven {
    setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
  }
}

dependencies {
  compile("edu.cmu.sphinx:sphinx4-core:5prealpha-SNAPSHOT")
  compile("com.mashape.unirest:unirest-java:1.4.7")
  compile("de.dfki.mary:marytts-runtime:5.2-SNAPSHOT")
  compile("de.dfki.mary:marytts-lang-en:5.2-SNAPSHOT")
  compile("de.dfki.mary:voice-cmu-slt-hsmm:5.2-SNAPSHOT")
  compile("org.codehaus.jettison:jettison:1.3.7")

  testCompile("junit:junit:4.12")
}