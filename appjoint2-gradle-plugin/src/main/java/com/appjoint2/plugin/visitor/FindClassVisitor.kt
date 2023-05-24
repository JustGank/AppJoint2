package com.appjoint2.plugin.visitor

import com.appjoint2.plugin.bean.AnnotationModuleSpecBean
import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor

class FindClassVisitor(val className: String, val interfaces: Array<String>) :
    ClassVisitor(ClassInfoRecord.ASM_OPCODES) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {

        Log.d("FindClassVisitor className : $className , descriptor : $descriptor")

        when (descriptor) {

            ClassInfoRecord.ASM_ANNOTATION_APP_SPEC -> {
                Log.i("FindClassVisitor find @AppSpec class : ${className} ")
                ClassInfoRecord.appSpecClass = className
            }

            ClassInfoRecord.ASM_ANNOTATION_MODULE_SPEC -> {
                return object : AnnotationMethodsVisitor() {
                    override fun visit(name: String?, value: Any?) {
                        val annotationModuleSpecBean = AnnotationModuleSpecBean(className)
                        if (value is Int) {
                            annotationModuleSpecBean.order = value
                        }
                        ClassInfoRecord.moduleSpecBeans.add(annotationModuleSpecBean)
                        Log.i("FindClassVisitor find @ModuleSpec class : $className , order : ${annotationModuleSpecBean.order} ")
                        super.visit(name, value)
                    }
                }
            }

            ClassInfoRecord.ASM_ANNOTATION_SERVICE_SPEC -> {
                return object : AnnotationMethodsVisitor() {
                    var valueSpecified = false
                    override fun visit(name: String?, value: Any?) {
                        super.visit(name, value)
                        valueSpecified = true
                        interfaces.forEach { interfaceName ->
                            val javaInterfaceName=interfaceName.replace("/",".")
                            Log.i("FindClassVisitor find @ServiceProvider class : $className , interfaceName : $javaInterfaceName , value : $value")
                            ClassInfoRecord.serviceSpecMap[Pair(
                                javaInterfaceName,
                                value.toString()
                            )] = className
                        }
                    }

                    override fun visitEnd() {
                        super.visitEnd()
                        if (!valueSpecified) {
                            interfaces.forEach { interfaceName ->
                                val javaInterfaceName=interfaceName.replace("/",".")
                                Log.i("FindClassVisitor find @ServiceProvider class : $className , interfaceName : $javaInterfaceName")
                                ClassInfoRecord.serviceSpecMap[Pair(
                                    javaInterfaceName,
                                    "AppJoint2"
                                )] = className
                            }
                        }
                    }
                }
            }
        }

        return super.visitAnnotation(descriptor, visible)
    }

}