plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.diffplug.spotless") version "8.5.0"
}

javafx {
    version = "26.0.1"
    modules = listOf("javafx.controls")
}

spotless {
    java {
        googleJavaFormat()
        target("src/**/*.java")
    }
}

group = "org.tcs"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (System.getenv("CI") == "true") {
        options.compilerArgs.add("-Werror")
    }
}