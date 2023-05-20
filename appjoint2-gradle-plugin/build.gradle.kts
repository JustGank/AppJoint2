plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("AppJointPlugin") {
            id = "com.appjoint2.plugin"
            implementationClass = "com.appjoint2.plugin.AppJoint2Plugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.android.tools.build:gradle:8.0.1")
    implementation("org.ow2.asm:asm-commons:9.4") // 包含 asm 以及 asm-tree
    implementation("org.ow2.asm:asm-util:9.4")
}

