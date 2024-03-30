plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.android.library") version "8.3.1" apply false
}

tasks.register("clean") {
    delete(rootProject.buildDir)
}