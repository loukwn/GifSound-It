plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.50.2"
}

include(
    ":app",
    ":data",
    ":domain",
    ":navigation",
    ":postdata",
    ":presentation:common",
    ":presentation:create",
    ":presentation:list",
    ":presentation:opengs",
    ":presentation:settings",
)
