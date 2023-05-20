package com.appjoint2.plugin.visitor

import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @Author:JustGank
 * */
class AddCallAppJoint2MethodVisitor(
    mv: MethodVisitor,
    val name: String,
    val desc: String,
    val aLoad: Boolean,
    val iLoad: Boolean
) : MethodVisitor(ClassInfoRecord.ASM_OPCODES, mv) {

    init {
        Log.d("AddCallAppJoint2MethodVisitor name : $name , desc : $desc , aLoad : $aLoad , iLoad : $iLoad")
    }

    override fun visitInsn(opcode: Int) {

        Log.d("AddCallAppJoint2MethodVisitor visitInsn opcode : $opcode ")


        if (opcode == Opcodes.IRETURN ||
            opcode == Opcodes.FRETURN ||
            opcode == Opcodes.ARETURN ||
            opcode == Opcodes.LRETURN ||
            opcode == Opcodes.DRETURN ||
            opcode == Opcodes.RETURN
        ) {

            val owner = ClassInfoRecord.ASM_APPJOINT_CLASS_PATH

            Log.d("AddCallAppJoint2MethodVisitor visitInsn owner : $owner ")

            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                owner,
                "get",
                "()${ClassInfoRecord.ASM_DESC_APPJOINT_CLASS_PATH}",
                false
            )

            if (aLoad) {
                mv.visitVarInsn(Opcodes.ALOAD, 1)
            }

            if (iLoad) {
                mv.visitVarInsn(Opcodes.ILOAD, 1)
            }

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc, false)


        }
        super.visitInsn(opcode)
    }

}