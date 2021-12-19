import de.fayard.refreshVersions.bootstrapRefreshVersions
import de.fayard.refreshVersions.migrateRefreshVersionsIfNeeded

include(":feat-create")


buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
////                                                      # available:0.10.0")
////                                                      # available:0.10.1")
////                                                      # available:0.11.0")
////                                                      # available:0.20.0")
////                                                      # available:0.21.0")
////                                                      # available:0.22.0")
////                                                      # available:0.23.0")
}

migrateRefreshVersionsIfNeeded("0.9.7") // Will be automatically removed by refreshVersions when upgraded to the latest version.

bootstrapRefreshVersions()

include(
    ":app",
    ":feat-opengs",
    ":feat-list",
    ":common",
    ":postdata",
    ":navigation",
    ":feat-settings"
)