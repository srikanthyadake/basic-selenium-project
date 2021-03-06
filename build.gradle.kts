
import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20"
    id("com.adarshr.test-logger") version "1.6.0"
    id("io.qameta.allure") version "2.7.0"
}

group = "io.github.christian-draeger"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

apply<TestLoggerPlugin>()
configure<TestLoggerExtension> {
    setTheme("mocha-parallel")
    slowThreshold = 6000
    showStandardStreams = true
}

dependencies {
    val fluentleniumVersion = "4.1.1"
    val seleniumVersion = "3.141.59"
    val webdriverManagerVersion = "3.3.0"
    val browsermobVersion = "2.1.5"

    implementation(kotlin("stdlib-jdk8"))

    testCompile("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testCompile("io.github.bonigarcia:webdrivermanager:$webdriverManagerVersion")
    testCompile("net.lightbody.bmp:browsermob-core:$browsermobVersion")
    compile("it.skrape:core:0.3.1")

    testCompile("org.assertj:assertj-core:3.12.0")
    testCompile("org.fluentlenium:fluentlenium-junit-jupiter:$fluentleniumVersion")
    testCompile("org.fluentlenium:fluentlenium-assertj:$fluentleniumVersion")
    testCompile("org.junit.jupiter:junit-jupiter:5.4.0")

    testCompile("org.awaitility:awaitility-kotlin:3.1.6")

    testCompile("io.github.microutils:kotlin-logging:1.6.25")
}

configurations {
    all {
        exclude(module = "junit")
        exclude(module = "htmlunit-driver")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        parallelTestExecution()
        
        systemProperty("browser", System.getProperty("browser"))

        finalizedBy("allureReport")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

fun Test.parallelTestExecution() {
    val parallel = "junit.jupiter.execution.parallel"
    systemProperties = mapOf(
        "$parallel.enabled" to true,
        "$parallel.mode.default" to "concurrent",
        "$parallel.config.dynamic.factor" to 4
    )
}

allure {
    autoconfigure = true
    version = "2.7.0"

    useJUnit5 {
        version = "2.7.0"
    }
}
