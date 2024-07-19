package com.xiaojinzi.component.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
abstract class LRouterAsmClassVisitor : AsmClassVisitorFactory<ParametersImpl> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        if (classContext.currentClassData.className == "com.xiaojinzi.component.support.ASMUtil") {
            val inputFiles = parameters.get().inputFiles.get()
            return InsertCodeVisitor(nextClassVisitor, inputFiles)
        }
        return nextClassVisitor
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className == "com.xiaojinzi.component.support.ASMUtil"
    }
}

