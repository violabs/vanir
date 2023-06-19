plugins {
	id("com.avast.gradle.docker-compose") version "0.16.12"
}

extra["testcontainersVersion"] = "1.18.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:r2dbc")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
}

dockerCompose {
	isRequiredBy(tasks.test)
	useComposeFiles.set(listOf("docker-compose.yml"))
}