description = "Example of sequence diagram generation"

dependencies {

    api(platform(Http4k.bom))
    implementation("dev.forkhandles:result4k:_")
    implementation(Http4k.aws)
    implementation(Http4k.cloudnative)
    implementation(Http4k.incubator)
    implementation(Http4k.client.okhttp)
    implementation(Http4k.server.undertow)

    testApi(Testing.junit.jupiter.api)
    testApi(Testing.junit.jupiter.engine)
    testApi(Http4k.testing.hamkrest)

    testFixturesApi(project(":lib"))
}
