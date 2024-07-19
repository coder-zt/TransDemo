package com.xiaojinzi.component.plugin

import org.gradle.api.file.Directory
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
class InsertCodeVisitor(
    nextVisitor: ClassVisitor,
    private val allRouterDir: List<Directory>
) : ClassVisitor(Opcodes.ASM9, nextVisitor) {

    private val allModels: List<HandleModel> by lazy {
        allRouterDir.asSequence()
            .flatMap { dir ->
                dir.asFileTree.matching {
                    it.include("**/**.kt")
                }
            }.filter {
                it.name.endsWith("ModuleGenerated.kt")
            }.mapNotNull {
                val className = it.absolutePath
                    .replace("\\", "/")
                    .substringAfter("kotlin/")
                    .removeSuffix(suffix = ".kt")
                val moduleName =
                    className.split("/").last().removeSuffix(suffix = "ModuleGenerated")
                return@mapNotNull HandleModel.Module(moduleName, className)
            }.toList()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return when (name) {
            "findModuleApplicationAsmImpl" -> FindModuleInstructAdapter(
                Opcodes.ASM9,
                mv,
                allModels.getTarget()
            )

            "getModuleNames" -> GetModuleInstructAdapter(
                Opcodes.ASM9,
                mv,
                allModels.getTarget()
            )

            else -> mv
        }
    }

    private inline fun <reified T : HandleModel> List<HandleModel>.getTarget(): List<T> {
        return filterIsInstance<T>().toList()
    }
}

