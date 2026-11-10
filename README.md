## Fabric 1.21.1 development

This branch is `1.21_fabric` and requires Java 21. In MultiWorkspace, build this mod with `./gradlew :McJtyLib:build` from the workspace root. Individual checkouts use the same `gradletools.gradle` driver, preferring local peer sources before the Jenkins/Maven fallback. Before the remote branch is available, select the workspace script with `-Pgradletools_path=../gradletools.gradle` and use `--gradle-user-home ../.gradle-user-home`.

McJtyLib
========

Companion library for all my mods (RFTools, Deep Resonance)
