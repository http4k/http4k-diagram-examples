description = "Tracing library"

dependencies {
    api(platform(Http4k.bom))
    api(Http4k.core)
    api(Http4k.format.moshi)
    api(Square.moshi.adapters)
    api(Testing.junit.jupiter.api)

    testApi(Http4k.testing.hamkrest)
    testApi(Http4k.testing.approval)
    testApi(Testing.junit.jupiter.engine)
}
