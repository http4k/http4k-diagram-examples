description = "Example of sequence diagram generation"

dependencies {
    api(platform(Http4k.bom))
    implementation("dev.forkhandles:result4k:_")
    implementation(Http4k.aws)
    implementation(Http4k.cloudnative)
    implementation(Http4k.incubator)
    implementation(Http4k.format.jackson)
    implementation(Http4k.client.okhttp)
    implementation(Http4k.server.undertow)
    implementation(Http4k.securityOauth)
    testImplementation(Http4k.testing.hamkrest)
    testImplementation(Http4k.testing.approval)
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.engine)
    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("com.nimbusds:nimbus-jose-jwt:_")
    implementation("org.bouncycastle:bcpkix-jdk15on:_")
}
