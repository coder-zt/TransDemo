package com.xiaojinzi.component.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.InstructionAdapter

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
class GetModuleInstructAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val moduleClass: List<HandleModel.Module>?
) : InstructionAdapter(api, methodVisitor) {

    override fun visitCode() {
        visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
        visitInsn(Opcodes.DUP)
        visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);

        // 将 ArrayList<Test2CacheMan> 对象存储到局部变量表中
        visitVarInsn(Opcodes.ASTORE, 0)
        moduleClass?.forEach {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitLdcInsn(it.moduleName);
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/util/ArrayList",
                "add",
                "(Ljava/lang/Object;)Z",
                false
            );
            mv.visitInsn(Opcodes.POP);
        }

        // 加载局部变量表中的 ArrayList<Test2CacheMan> 对象到操作数栈顶
        visitVarInsn(Opcodes.ALOAD, 0);

        // 返回 ArrayList<Test2CacheMan> 对象
        visitInsn(Opcodes.ARETURN);
        super.visitCode()
    }

}