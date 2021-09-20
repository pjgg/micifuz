load("@rules_jvm_external//:defs.bzl", "maven_install")

## RUNTIME
VERTX_VERSION = "4.1.3"
NETTY_TRANSPORT_VERSION = "4.1.68.Final"

## TESTS
ASSERTJ_VERSION = "3.20.2"
JUNIT_VERSION = "5.7.0"
JUNIT_PLATFORM_VERSION = "1.7.0"
HTTP_CLIENT_VERSION = "4.5.13"
REST_ASSURE_VERSION = "4.4.0"
MOCKITO_VERSION = "3.12.4"
HAMCREST_VERSION = "1.3"
COMMONS_LANGS3_VERSION = "3.12.0"

def maven():
    maven_install(
        name = "vertx",
        artifacts = [
            "io.vertx:vertx-web:%s" % VERTX_VERSION,
            "io.vertx:vertx-health-check:%s" % VERTX_VERSION,
            "io.vertx:vertx-web-client:%s" % VERTX_VERSION,
            "io.vertx:vertx-core:%s" % VERTX_VERSION,
            "io.vertx:vertx-rx-java2:%s" % VERTX_VERSION,
            "io.vertx:vertx-rx-java2:%s" % VERTX_VERSION,
            "io.vertx:vertx-config:%s" % VERTX_VERSION,
            "io.vertx:vertx-config-yaml:%s" % VERTX_VERSION,
            "io.netty:netty-transport:%s" % NETTY_TRANSPORT_VERSION,
            "org.apache.commons:commons-lang3:%s" % COMMONS_LANGS3_VERSION,
        ],
        repositories = [
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
    )

    maven_install(
        name = "vertx_tests",
        artifacts = [
            "io.vertx:vertx-junit5:%s" % VERTX_VERSION,
            "org.assertj:assertj-core:%s" % ASSERTJ_VERSION,
            "org.junit.jupiter:junit-jupiter-engine:%s" % JUNIT_VERSION,
            "org.junit.jupiter:junit-jupiter-api:%s" % JUNIT_VERSION,
            "org.junit.platform:junit-platform-console:%s" % JUNIT_PLATFORM_VERSION,
            "org.apache.httpcomponents:httpclient:%s" % HTTP_CLIENT_VERSION,
            "io.rest-assured:rest-assured:%s" % REST_ASSURE_VERSION,
            "org.hamcrest:hamcrest-all:%s" % HAMCREST_VERSION,
        ],
        repositories = [
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
    )

    maven_install(
        name = "mockito",
        artifacts = [
            "org.mockito:mockito-junit-jupiter:%s" % MOCKITO_VERSION,
            "org.mockito:mockito-core:%s" % MOCKITO_VERSION,
        ],
        repositories = [
            "https://repo1.maven.org/maven2",
        ],
        fetch_sources = True,
    )
