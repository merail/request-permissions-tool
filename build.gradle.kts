plugins {
    id("com.android.application") version "7.1.0" apply false
    id("com.android.library") version "7.1.0" apply false
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}