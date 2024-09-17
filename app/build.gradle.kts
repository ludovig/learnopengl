/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.5/userguide/building_java_projects.html in the Gradle documentation.
 */

val lwjglVersion = "3.3.3"
val lwjglNatives = "natives-linux"

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven("https://raw.githubusercontent.com/kotlin-graphics/mary/master")
}

dependencies {
    implementation("io.github.kotlin-graphics:glm:0.9.9.1-12")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-jemalloc")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-remotery")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-remotery", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)

    // This dependency is used by the application.
    implementation(libs.guava)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("learnopengl.AppKt")
}

tasks.register<JavaExec>("hello_window") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.HelloWindowKt")
}
tasks.register<JavaExec>("hello_window_clear") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.HelloWindowClearKt")
}
tasks.register<JavaExec>("hello_triangle") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.HelloTriangleKt")
}
tasks.register<JavaExec>("hello_triangle_indexed") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.HelloTriangleIndexedKt")
}
tasks.register<JavaExec>("shaders_uniform") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.ShadersUniformKt")
}
tasks.register<JavaExec>("shaders_interpolation") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.ShadersInterpolationKt")
}
tasks.register<JavaExec>("shaders_class") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.ShadersClassKt")
}
tasks.register<JavaExec>("textures") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.TexturesKt")
}
tasks.register<JavaExec>("textures_combined") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.TexturesCombinedKt")
}
tasks.register<JavaExec>("transformations") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.TransformationsKt")
}
tasks.register<JavaExec>("coordinate_systems") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CoordinateSystemsKt")
}
tasks.register<JavaExec>("coordinate_systems_depth") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CoordinateSystemsDepthKt")
}
tasks.register<JavaExec>("coordinate_systems_multiple") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CoordinateSystemsMultipleKt")
}
tasks.register<JavaExec>("camera_circle") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CameraCircleKt")
}
tasks.register<JavaExec>("camera_keyboard_dt") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CameraKeyboardDtKt")
}
tasks.register<JavaExec>("camera_mouse_zoom") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CameraMouseZoomKt")
}
tasks.register<JavaExec>("camera_class") {
    dependsOn("classes")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("learnopengl.a_getting_started.CameraClassKt")
}
