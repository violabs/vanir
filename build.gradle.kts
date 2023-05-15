import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6" apply false
	id("io.spring.dependency-management") version "1.1.0" apply false
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

buildscript {
	repositories {
		mavenCentral()
	}
}

allprojects {
	group = "io.violabs"
	version = "0.0.1-SNAPSHOT"

	tasks.withType<JavaCompile> {
		sourceCompatibility = JavaVersion.VERSION_17.majorVersion
		targetCompatibility = JavaVersion.VERSION_17.majorVersion
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = JavaVersion.VERSION_17.majorVersion
		}
	}

	tasks.withType<Test> {
		systemProperty("spring.profiles.active", "test")

		reports {
			html.required.set(true)
			junitXml.required.set(true)
		}

		useJUnitPlatform()
	}
}

subprojects {
	repositories {
		mavenCentral()
	}

	apply {
		plugin("io.spring.dependency-management")
	}
}
