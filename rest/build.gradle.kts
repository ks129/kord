dependencies {
    api(common)

    api(Dependencies.`ktor-client-json`)
    api(Dependencies.`ktor-client-json-jvm`)
    api(Dependencies.`ktor-client-serialization-jvm`)
    api(Dependencies.`ktor-client-cio`)

    testImplementation(Dependencies.`ktor-client-mock`)
    testImplementation(Dependencies.`ktor-client-mock-jvm`)
}
