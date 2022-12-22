import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("com.adarshr:gradle-test-logger-plugin:_")
    }
}

plugins {
    `java-test-fixtures`
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-test-fixtures")

    repositories {
        mavenCentral()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
}
