package com.xiaojinzi.component.plugin

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.InstructionAdapter

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */

class FindModuleInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val moduleClass: List<HandleModel.Module>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        moduleClass?.forEach {
            mv.visitLdcInsn(it.moduleName)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z",
                false
            )
            val labelQa = Label()
            mv.visitJumpInsn(Opcodes.IFEQ, labelQa)
            mv.visitTypeInsn(Opcodes.NEW, it.className)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, it.className, "<init>", "()V", false)
            mv.visitInsn(Opcodes.ARETURN)
            mv.visitLabel(labelQa)
        }
        super.visitCode()
    }

}