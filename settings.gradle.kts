plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.50.2"
}

include(
    ":app",
    ":feat-opengs",
    ":feat-list",
    ":common",
    ":postdata",
    ":navigation",
    ":feat-settings"
)