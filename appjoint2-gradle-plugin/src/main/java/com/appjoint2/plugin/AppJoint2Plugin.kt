package com.appjoint2.plugin


import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import com.appjoint2.plugin.bean.LogConfig
import com.appjoint2.plugin.task.AppJoint2ClassTask
import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register


/**
 * @Author:JustGank
 * */
class AppJoint2Plugin : Plugin<Project> {

    override fun apply(project: Project) {

        val logConfig = project.extensions.create("AppJoint2LogConfig", LogConfig::class.java)

        project.afterEvaluate {
            Log.logConfig = logConfig
            Log.logConfig.apply { Log.i("logConfig openLog : $openLog , debug : $debug , outputTime : $outputTime") }
            ClassInfoRecord.clearCache()
        }

        with(project) {

            plugins.withType(AppPlugin::class.java) {

                val androidComponents =
                    extensions.findByType(AndroidComponentsExtension::class.java)

                androidComponents?.onVariants { variant ->

                    val name = "AppJoint2${firstCharToUpperCase(variant.name)}Task"

                    Log.d("apply register task name : $name")

                    val taskProvider = tasks.register<AppJoint2ClassTask>(name)
                    {
                        group = "AppJoint2"
                        description =
                            "Generate route tables after inject to main application and core."
                        bootClasspath.set(androidComponents.sdkComponents.bootClasspath)
                        classpath = variant.compileClasspath
                    }

                    variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                        .use(taskProvider)
                        .toTransform(
                            ScopedArtifact.CLASSES,
                            AppJoint2ClassTask::jars,
                            AppJoint2ClassTask::dirs,
                            AppJoint2ClassTask::output,
                        )
                }

            }
        }

        Log.i("========== AppJoint2 Plugin Initialized! ==========")

    }


    fun firstCharToUpperCase(input: String): String {
        val firstChar: Char = input.first()
        return input.replaceFirst(firstChar, Character.toUpperCase(firstChar))
    }


}