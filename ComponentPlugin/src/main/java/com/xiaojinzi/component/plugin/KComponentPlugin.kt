package com.xiaojinzi.component.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KComponentPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            plugins.withType(AppPlugin::class.java) {
                val androidComponents =
                    extensions.findByType(AndroidComponentsExtension::class.java)
                androidComponents?.onVariants { variant ->
                    // 生成asm模板文件
                    val addSourceTaskProvider = project.tasks.register(
                        "${variant.name}ASMStubClass",
                        ASMStubClassTask::class.java
                    )
                    variant.sources.java?.addGeneratedSourceDirectory(
                        addSourceTaskProvider,
                        ASMStubClassTask::outputFolder
                    )


                    val generatedDir = "generated/ksp/"
                    variant.instrumentation.transformClassesWith(
                        LRouterAsmClassVisitor::class.java,
                        InstrumentationScope.PROJECT
                    ) { param ->
                        val list = project.rootProject.subprojects.plus(project)
                            .map { it.layout.buildDirectory.dir(generatedDir).get() }
                        param.inputFiles.set(list)
                    }
                    variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
                    variant.instrumentation.excludes.addAll(
                        "androidx/**",
                        "android/**",
                        "com/google/**",
                    )
                }
            }
        }

    }
}