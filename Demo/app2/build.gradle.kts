import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.InstructionAdapter


/*apply plugin: 'com.android.application'
apply plugin: 'com.xiaojinzi.component.plugin'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'*/

plugins {
    id("com.android.application")
    id("com.xiaojinzi.kcomponent.plugin")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

apply(from = "../demo_module.gradle")

ksp {
    arg("ModuleName", "app2")
}

/*kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}*/

android {

    namespace = "com.xiaojinzi.component.app2"

//    defaultConfig {
//        multiDexEnabled = true
//        multiDexKeepProguard = file("multiDexKeep.pro")
//    }
    signingConfigs {
        create("release") {
            storeFile = file("sign")
            storePassword = "xiaojinzi"
            keyAlias = "xiaojinzi"
            keyPassword = "xiaojinzi"
        }
        getByName("debug") {
            storeFile = file("sign")
            storePassword = "xiaojinzi"
            keyAlias = "xiaojinzi"
            keyPassword = "xiaojinzi"
        }
    }

    buildTypes {
        getByName("release") {
            ndk { abiFilters.add("arm64-v8a") }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            ndk { abiFilters.add("arm64-v8a") }
            isMinifyEnabled = false
//            multiDexKeepProguard = file("multiDexKeep.pro")
//            multiDexKeepFile = file("mutlidex-config.txt")
            signingConfig = signingConfigs.getByName("release")
        }

    }

}

dependencies {
    implementation(project(":Demo:module-support"))
    implementation(project(":Demo:module-user"))
    implementation(project(":Demo:module-base"))
}

//androidComponents {
//    val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
//    androidComponents.onVariants { variant ->
//        // 生成asm模板文件
//        val addSourceTaskProvider = project.tasks.register(
//            "${variant.name}CleanStubClass",
//            ASMStubClassTask::class.java
//        )
//        println("variant.sources.java ====> ${variant.sources.java}")
//        variant.sources.java?.addGeneratedSourceDirectory(
//            addSourceTaskProvider,
//            ASMStubClassTask::outputFolder
//        )
//
//
//        val generatedDir = "generated/ksp/"
//        variant.instrumentation.transformClassesWith(
//            LRouterAsmClassVisitor::class.java,
//            InstrumentationScope.PROJECT
//        ) { param ->
//            println("param ===>")
//            param.genDirName.set(generatedDir)
//            val list = project.rootProject.subprojects.plus(project)
//                .map { it.layout.buildDirectory.dir(generatedDir).get() }
//            param.inputFiles.set(list)
//        }
//        variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
//        variant.instrumentation.excludes.addAll(
//            "androidx/**",
//            "android/**",
//            "com/google/**",
//        )
//    }
//}

//abstract class ASMStubClassTask : DefaultTask() {
//
//    @get:OutputDirectory
//    abstract val outputFolder: DirectoryProperty
//
//    @TaskAction
//    fun taskAction() {
//        val outputFile =
//            File(outputFolder.asFile.get(), "com/xiaojinzi/component/support/ASMUtil.java")
//        outputFile.parentFile.mkdirs()
//        outputFile.writeText(
//            """
//                package com.xiaojinzi.component.support;
//
//                import androidx.annotation.Keep;
//                import com.xiaojinzi.component.impl.IModuleLifecycle;
//                import java.util.ArrayList;
//                import java.util.List;
//
//                @Keep
//                public class ASMUtil {
//
//                    public static IModuleLifecycle findModuleApplicationAsmImpl(String str) {
//                        return null;
//                    }
//
//                    public static List getModuleNames() {
//                        ArrayList arrayList = new ArrayList();
//                        return arrayList;
//                    }
//                }
//            """.trimIndent()
//        )
//    }
//}
//
//abstract class LRouterAsmClassVisitor : AsmClassVisitorFactory<ParametersImpl> {
//
//    override fun createClassVisitor(
//        classContext: ClassContext,
//        nextClassVisitor: ClassVisitor
//    ): ClassVisitor {
//        println("createClassVisitor =====>  ${classContext.currentClassData.className}")
//        if (classContext.currentClassData.className == "com.xiaojinzi.component.support.ASMUtil") {
//            val inputFiles = parameters.get().inputFiles.get()
//            val genDirName = parameters.get().genDirName.get()
//            inputFiles.forEach {
//                println("inputFiles ====> $it")
//            }
//            return InsertCodeVisitor(nextClassVisitor, inputFiles, genDirName)
//        }
//        return nextClassVisitor
//    }
//
//    override fun isInstrumentable(classData: ClassData): Boolean {
////        println("createClassVisitor =====> ")
//        return classData.className == "com.xiaojinzi.component.support.ASMUtil"
//    }
//}
//
//
//interface ParametersImpl : InstrumentationParameters {
//
//    @get:Internal
//    val genDirName: Property<String>
//
//    @get:Internal
//    val inputFiles: ListProperty<Directory>
//}
//
//class InsertCodeVisitor(
//    nextVisitor: ClassVisitor,
//    private val allRouterDir: List<Directory>,
//    private val genDirName: String
//) : ClassVisitor(Opcodes.ASM9, nextVisitor) {
//
//    private val allModels: List<HandleModel> by lazy {
//        allRouterDir.asSequence()
//            .flatMap { dir ->
//                dir.asFileTree.matching {
//                    include("**/**.kt")
//                }
//            }.filter {
//                it.name.endsWith("ModuleGenerated.kt")
//            }
//            //
//            //.removeSuffix(suffix = "ModuleGenerated") to "${it.name}.class"
//            .mapNotNull {
//                val className = it.absolutePath
//                    .replace("\\", "/")
////                    .substringAfter(genDirName)
////                    .removePrefix(prefix = "com.xiaojinzi.component.impl.")
//                    .removeSuffix(suffix = ".kt")
//                val moduleName =
//                    className.split("/").last().removeSuffix(suffix = "ModuleGenerated")
//                println("className ===> $className, moduleName ===> $moduleName")
//                return@mapNotNull HandleModel.Module(moduleName, className)
//            }.toList()
//    }
//
//    override fun visitMethod(
//        access: Int,
//        name: String?,
//        descriptor: String?,
//        signature: String?,
//        exceptions: Array<out String>?
//    ): MethodVisitor {
//        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
//        println("InsertCodeVisitor::visitMethod() name:$name mv:$mv")
//        return when (name) {
//            "findModuleApplicationAsmImpl" -> FindModuleInstructAdapter(
//                Opcodes.ASM9,
//                mv,
//                allModels.getTarget()
//            )
//
//            "getModuleNames" -> GetModuleInstructAdapter(
//                Opcodes.ASM9,
//                mv,
//                allModels.getTarget()
//            )
//
//            else -> mv
//        }
//    }
//
//    private inline fun <reified T : HandleModel> List<HandleModel>.getTarget(): List<T> {
//        return filterIsInstance<T>().toList()
//    }
//}
//
//
//sealed class HandleModel {
//
//    data class Autowired(val className: String) : HandleModel()
//
//    data class Module(val moduleName: String, val className: String) : HandleModel()
//}
//
//class FindModuleInstructAdapter(
//    api: Int,
//    methodVisitor: MethodVisitor,
//    private val moduleClass: List<HandleModel.Module>?
//) : InstructionAdapter(api, methodVisitor) {
//
//    override fun visitCode() {
//        moduleClass?.forEach {
//            mv.visitLdcInsn(it.moduleName)
//            mv.visitVarInsn(Opcodes.ALOAD, 0)
//            mv.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "java/lang/String",
//                "equals",
//                "(Ljava/lang/Object;)Z",
//                false
//            )
//            val labelQa = Label()
//            mv.visitJumpInsn(Opcodes.IFEQ, labelQa)
//            mv.visitTypeInsn(Opcodes.NEW, it.className)
//            mv.visitInsn(Opcodes.DUP)
//            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, it.className, "<init>", "()V", false)
//            mv.visitInsn(Opcodes.ARETURN)
//            mv.visitLabel(labelQa)
//        }
//        super.visitCode()
//    }
//
//}
//
//class GetModuleInstructAdapter(
//    api: Int,
//    methodVisitor: MethodVisitor,
//    private val moduleClass: List<HandleModel.Module>?
//) : InstructionAdapter(api, methodVisitor) {
//
//    override fun visitCode() {
//        visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
//        visitInsn(Opcodes.DUP)
//        visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
//
//        // 将 ArrayList<Test2CacheMan> 对象存储到局部变量表中
//        visitVarInsn(Opcodes.ASTORE, 0)
//        moduleClass?.forEach {
//            println("GetModuleInstructAdapter ===> class:$it")
//            mv.visitVarInsn(Opcodes.ALOAD, 0)
//            mv.visitLdcInsn(it.moduleName);
//            mv.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "java/util/ArrayList",
//                "add",
//                "(Ljava/lang/Object;)Z",
//                false
//            );
//            mv.visitInsn(Opcodes.POP);
//        }
//
//        // 加载局部变量表中的 ArrayList<Test2CacheMan> 对象到操作数栈顶
//        visitVarInsn(Opcodes.ALOAD, 0);
//
//        // 返回 ArrayList<Test2CacheMan> 对象
//        visitInsn(Opcodes.ARETURN);
//        super.visitCode()
//    }
//
//}
