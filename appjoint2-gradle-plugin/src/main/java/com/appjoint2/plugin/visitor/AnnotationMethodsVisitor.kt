package com.appjoint2.plugin.visitor

import com.appjoint2.plugin.util.ClassInfoRecord
import org.objectweb.asm.AnnotationVisitor

/**
 * @Author:JustGank
 * */
open class AnnotationMethodsVisitor: AnnotationVisitor(ClassInfoRecord.ASM_OPCODES) {

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
    }
}