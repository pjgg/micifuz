COMMON_DEPS = [
    "//backend/commons:deps",
    "@vertx//:io_netty_netty_transport",
    "@vertx//:io_reactivex_rxjava2_rxjava",
    "@vertx//:io_vertx_vertx_config",
    "@vertx//:io_vertx_vertx_config_yaml",
    "@vertx//:io_vertx_vertx_core",
    "@vertx//:io_vertx_vertx_health_check",
    "@vertx//:io_vertx_vertx_rx_java2",
    "@vertx//:io_vertx_vertx_web",
    "@vertx//:io_vertx_vertx_web_client",
    "@vertx//:org_apache_commons_commons_lang3",
]

COMMON_TESTS_DEPS = [
    "//backend/tests-resources:deps",
    "@mockito//:org_mockito_mockito_core",
    "@mockito//:org_mockito_mockito_junit_jupiter",
    "@vertx_tests//:io_rest_assured_rest_assured",
    "@vertx_tests//:io_vertx_vertx_junit5",
    "@vertx_tests//:org_apache_httpcomponents_httpclient",
    "@vertx_tests//:org_assertj_assertj_core",
    "@vertx_tests//:org_hamcrest_hamcrest_all",
    "@vertx_tests//:org_junit_jupiter_junit_jupiter_api",
    "@vertx_tests//:org_junit_platform_junit_platform_console",
]
