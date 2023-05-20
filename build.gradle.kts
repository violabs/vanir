import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6" apply false
	id("io.spring.dependency-management") version "1.1.0" apply false
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

allprojects {
	group = "io.violabs"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
		maven { url = uri("https://jitpack.io") }
	}

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
	apply {
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.jvm")
	}

	dependencies {

	}

	when (name) {
		"freya", "freyr" -> {
			dependencies {
				implementation(project(":core"))
				implementation("org.springframework.boot:spring-boot-starter-actuator")
				implementation("org.springframework.boot:spring-boot-starter-webflux")
				implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
				implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
				implementation("org.jetbrains.kotlin:kotlin-reflect")

				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

				annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
				testImplementation("com.github.violabs:wesley:1.1.2")
				testImplementation("org.springframework.boot:spring-boot-starter-test")
				testImplementation("io.projectreactor:reactor-test")
				testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
			}
		}
	}
}
