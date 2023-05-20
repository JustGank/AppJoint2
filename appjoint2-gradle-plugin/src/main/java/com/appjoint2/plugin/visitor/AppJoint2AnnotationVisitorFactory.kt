package com.appjoint2.plugin.visitor

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.appjoint2.plugin.bean.AnnotationModuleSpecBean
import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor


/**
 * @Author:JustGank
 * */
abstract class AppJoint2AnnotationVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return object : ClassVisitor(ClassInfoRecord.ASM_OPCODES, nextClassVisitor) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
                val className = classContext.currentClassData.className

                Log.d("AppJoint2AnnotationVisitorFactory createClassVisitor className : $className , descriptor : $descriptor")

                when (descriptor) {

                    ClassInfoRecord.ASM_ANNOTATION_APP_SPEC -> {
                        Log.i("find @AppSpec class : ${className} ")
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
                                Log.i("find @ModuleSpec class : $className , order : ${annotationModuleSpecBean.order} ")
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
                                classContext.currentClassData.interfaces.forEach { interfaceName ->
                                    Log.i("find @ServiceProvider class : $className , interfaceName : $interfaceName , value : $value")
                                    ClassInfoRecord.serviceSpecMap[Pair(
                                        interfaceName,
                                        value.toString()
                                    )] = className
                                }
                            }

                            override fun visitEnd() {
                                super.visitEnd()
                                if (!valueSpecified) {
                                    classContext.currentClassData.interfaces.forEach { interfaceName ->
                                        Log.i("find @ServiceProvider class : $className , interfaceName : $interfaceName")
                                        ClassInfoRecord.serviceSpecMap[Pair(
                                            interfaceName,
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
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val annotations = classData.classAnnotations
        return annotations.contains(ClassInfoRecord.ANNOTATION_APP_SPEC) ||
                annotations.contains(ClassInfoRecord.ANNOTATION_MODULE_SPEC) ||
                annotations.contains(ClassInfoRecord.ANNOTATION_SERVICE_SPEC)
    }

}