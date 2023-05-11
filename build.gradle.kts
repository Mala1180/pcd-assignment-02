import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    java
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}


dependencies {
    implementation("io.vertx:vertx-core:4.4.1")
    if (Os.isFamily(Os.FAMILY_MAC)) {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.76.Final:osx-aarch_64")
    }
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.76.Final:osx-aarch_64")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.4")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}