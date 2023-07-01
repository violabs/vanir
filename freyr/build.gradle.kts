
val okhttp3Version = "5.0.0-alpha.11"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttp3Version")
    testImplementation("com.squareup.okhttp3:okhttp:$okhttp3Version")

}