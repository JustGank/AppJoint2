package com.appjoint2.plugin.util

import com.appjoint2.plugin.bean.AnnotationModuleSpecBean
import org.objectweb.asm.Opcodes
import java.lang.IllegalArgumentException

/**
 * @Author:JustGank
 * */
object ClassInfoRecord {

    val ASM_OPCODES = Opcodes.ASM5

    val APPJOINT_CLASS_PATH = "com.appjoint2.core.AppJoint2"
    val ASM_APPJOINT_CLASS_PATH = APPJOINT_CLASS_PATH.replace(".", "/")
    val ASM_DESC_APPJOINT_CLASS_PATH="L$ASM_APPJOINT_CLASS_PATH;"

    private val ANNOTATION_PACKAGE = "com.appjoint2.annotations"

    val ANNOTATION_APP_SPEC = "$ANNOTATION_PACKAGE.AppSpec"
    val ANNOTATION_MODULE_SPEC = "$ANNOTATION_PACKAGE.ModuleSpec"
    val ANNOTATION_SERVICE_SPEC = "$ANNOTATION_PACKAGE.ServiceProvider"

    val ASM_ANNOTATION_APP_SPEC = "L${ANNOTATION_APP_SPEC.replace(".", "/")};"
    val ASM_ANNOTATION_MODULE_SPEC = "L${ANNOTATION_MODULE_SPEC.replace(".", "/")};"
    val ASM_ANNOTATION_SERVICE_SPEC = "L${ANNOTATION_SERVICE_SPEC.replace(".", "/")};"

    var appSpecClass: String = ""
        set(value) {
            if (field == "" || field != "" && value == "") {
                field = value
            } else {
                throw IllegalArgumentException("One project only have one 'AppSpec' annotation in main application! ")
            }
        }

    val moduleSpecBeans: ArrayList<AnnotationModuleSpecBean> = ArrayList()
    val serviceSpecMap: HashMap<Pair<String, String>, String> = HashMap()

    fun clearCache() {
        appSpecClass = ""
        moduleSpecBeans.clear()
        serviceSpecMap.clear()
        Log.i("ClassInfoRecord clearCache moduleSpecBeans size :${moduleSpecBeans.size} , serviceSpecSet size : ${serviceSpecMap.size}")
    }

    fun outputCurrenCacheInfo(){
        Log.i("ClassInfoRecord outputCurrenCacheInfo moduleSpecBeans size :${moduleSpecBeans.size} , serviceSpecSet size : ${serviceSpecMap.size} ")
    }


}