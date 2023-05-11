plugins {
    java
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}


dependencies {
    implementation("io.vertx:vertx-core:4.4.1")
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.76.Final:osx-aarch_64")
    implementation("io.reactivex.rxjava3:rxjava:3.1.4")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}