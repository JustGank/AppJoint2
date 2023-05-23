package com.appjoint2.plugin.visitor


import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


/**
 * @Author:JustGank
 * */
class AppJoint2ClassVisitor(
    cv: ClassVisitor
) : ClassVisitor(ClassInfoRecord.ASM_OPCODES, cv) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        /**
         *     int ACC_PUBLIC = 0x0001; // class, field, method
         *     int ACC_PRIVATE = 0x0002; // class, field, method
         *     int ACC_PROTECTED = 0x0004; // class, field, method
         *     int ACC_STATIC = 0x0008; // field, method
         *     int ACC_FINAL = 0x0010; // class, field, method, parameter
         *     int ACC_SUPER = 0x0020; // class
         *     int ACC_SYNCHRONIZED = 0x0020; // method
         *     int ACC_VOLATILE = 0x0040; // field
         *
         *     此处映射为 AppJoint 类中的 私有构造方法 private AppJoint() {}*     如果此方法是 AppJoint 的构造方法 使用自定义类访问器访问
         *
         * */

        Log.d("AppJoint2ClassVisitor methodVisitor access : $access , name : $name , descriptor : $descriptor")

        if (access == 2 && name == "<init>" && descriptor == "()V") {
            return MethodVisitorAddCodeToConstructor(methodVisitor)
        }
        return methodVisitor
    }


    inner class MethodVisitorAddCodeToConstructor(mv: MethodVisitor) :
        MethodVisitor(ClassInfoRecord.ASM_OPCODES, mv) {

        override fun visitInsn(opcode: Int) {

            Log.d("MethodVisitorAddCodeToConstructor visitInsn opcode : $opcode")

            if (opcode == Opcodes.IRETURN ||
                opcode == Opcodes.FRETURN ||
                opcode == Opcodes.ARETURN ||
                opcode == Opcodes.LRETURN ||
                opcode == Opcodes.DRETURN ||
                opcode == Opcodes.RETURN
            ) {

                insertInitParams()

                val moduleApplications = ClassInfoRecord.moduleSpecBeans
                moduleApplications.sortWith(compareBy { it.order })
                moduleApplications.forEach { it ->
                    val convertClass = it.className
                    Log.i("insertApplicationAdd className:${convertClass} , order:${it.order} ")
                    insertApplicationAdd(convertClass)
                }

                ClassInfoRecord.serviceSpecMap.forEach { router: Pair<String, String>, impl: String ->

                    insertRoutersPut(router, impl)
                }
            }
            super.visitInsn(opcode)
        }


        fun insertInitParams() {

            Log.d("MethodVisitorAddCodeToConstructor insertInitParams")

            val label1 = Label()
            mv.visitLabel(label1)
            mv.visitLineNumber(20, label1)
            mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/util/ArrayList",
                "<init>",
                "()V",
                false
            )
            mv.visitFieldInsn(
                Opcodes.PUTSTATIC,
                ClassInfoRecord.ASM_APPJOINT_CLASS_PATH,
                "moduleApplications",
                "Ljava/util/List;"
            )
            val label2 = Label()
            mv.visitLabel(label2)
            mv.visitLineNumber(22, label2)
            mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false
            )
            mv.visitFieldInsn(
                Opcodes.PUTSTATIC,
                ClassInfoRecord.ASM_APPJOINT_CLASS_PATH,
                "routersMap",
                "Ljava/util/HashMap;"
            )
            val label3 = Label()
            mv.visitLabel(label3)
            mv.visitLineNumber(24, label3)
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/ref/SoftReference")
            mv.visitInsn(Opcodes.DUP)
            mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/ref/SoftReference",
                "<init>",
                "(Ljava/lang/Object;)V",
                false
            )
            mv.visitFieldInsn(
                Opcodes.PUTSTATIC,
                ClassInfoRecord.ASM_APPJOINT_CLASS_PATH,
                "softRouterInstanceMap",
                "Ljava/lang/ref/SoftReference;"
            )
        }

        fun insertApplicationAdd(applicationName: String) {

            Log.d("MethodVisitorAddCodeToConstructor insertApplicationAdd applicationName : $applicationName")

            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                ClassInfoRecord.ASM_APPJOINT_CLASS_PATH,
                "moduleApplications",
                "Ljava/util/List;"
            )
            mv.visitTypeInsn(Opcodes.NEW, applicationName)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, applicationName, "<init>", "()V", false)
            mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/List",
                "add",
                "(Ljava/lang/Object;)Z",
                true
            )

        }


        fun insertRoutersPut(router: Pair<String, String>, impl: String) {

            Log.d("MethodVisitorAddCodeToConstructor insertRoutersPut")

            mv.visitFieldInsn(
                Opcodes.GETSTATIC, ClassInfoRecord.ASM_APPJOINT_CLASS_PATH, "routersMap",
                "Ljava/util/HashMap;"
            )

            val routerKey="${router.first}&${router.second}"
            val convertImpl = impl.replace(".", "/")

            Log.i("MethodVisitorAddCodeToConstructor insertRoutersPut routerKey : $routerKey , routerValue : $convertImpl")

            mv.visitLdcInsn(routerKey)
            mv.visitLdcInsn(Type.getObjectType(convertImpl))
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/util/HashMap",
                "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true
            )

        }


    }


}