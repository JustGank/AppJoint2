package com.appjoint2.plugin.visitor

import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @Author:JustGank
 * */
class ApplicationClassVisitor(cv: ClassVisitor) : ClassVisitor(ClassInfoRecord.ASM_OPCODES, cv) {

    var onCreateDefined = false
    var attachBaseContextDefined = false
    var onConfigurationChangedDefined = false
    var onLowMemoryDefined = false
    var onTerminateDefined = false
    var onTrimMemoryDefined = false

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        Log.d("ApplicationClassVisitor visitMethod name&descriptor : ${name + descriptor}")

        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        when (name + descriptor) {
            "onCreate()V" -> {
                onCreateDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), false, false
                )
            }

            "attachBaseContext(Landroid/content/Context;)V" -> {
                attachBaseContextDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), true, false
                )
            }

            "onConfigurationChanged(Landroid/content/res/Configuration;)V" -> {
                onConfigurationChangedDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), true, false
                )
            }

            "onLowMemory()V" -> {
                onLowMemoryDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), false, false
                )
            }

            "onTerminate()V" -> {
                onTerminateDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), false, false
                )
            }

            "onTrimMemory(I)V" -> {
                onTrimMemoryDefined = true
                return AddCallAppJoint2MethodVisitor(
                    methodVisitor, name.toString(),
                    descriptor.toString(), false, true
                )
            }
        }
        return methodVisitor
    }

    override fun visitEnd() {

        Log.d("ApplicationClassVisitor visitEnd attachBaseContextDefined : $attachBaseContextDefined")

        if (!attachBaseContextDefined) {
            defineMethod(4, "attachBaseContext", "(Landroid/content/Context;)V", true, false)
        }

        Log.d("ApplicationClassVisitor visitEnd onCreateDefined : $onCreateDefined")

        if (!onCreateDefined) {
            defineMethod(1, "onCreate", "()V", false, false)
        }

        Log.d("ApplicationClassVisitor visitEnd onConfigurationChangedDefined : $onConfigurationChangedDefined")

        if (!onConfigurationChangedDefined) {
            defineMethod(
                1,
                "onConfigurationChanged",
                "(Landroid/content/res/Configuration;)V",
                true,
                false
            )
        }

        Log.d("ApplicationClassVisitor visitEnd onLowMemoryDefined : $onLowMemoryDefined")

        if (!onLowMemoryDefined) {
            defineMethod(1, "onLowMemory", "()V", false, false)
        }

        Log.d("ApplicationClassVisitor visitEnd onTerminateDefined : $onTerminateDefined")

        if (!onTerminateDefined) {
            defineMethod(1, "onTerminate", "()V", false, false)
        }

        Log.d("ApplicationClassVisitor visitEnd onTrimMemoryDefined : $onTrimMemoryDefined")

        if (!onTrimMemoryDefined) {
            defineMethod(1, "onTrimMemory", "(I)V", false, true)
        }

        super.visitEnd()
    }


    fun defineMethod(access: Int, name: String, desc: String, aLoad1: Boolean, iLoad1: Boolean) {
        val methodVisitor = this.visitMethod(access, name, desc, null, null)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        if (aLoad1) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        }
        if (iLoad1) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        }
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "android/app/Application",
            name,
            desc,
            false
        )
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()

    }


}