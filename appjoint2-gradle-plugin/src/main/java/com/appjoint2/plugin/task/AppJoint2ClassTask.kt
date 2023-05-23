package com.appjoint2.plugin.task

import com.appjoint2.plugin.util.ClassInfoRecord
import com.appjoint2.plugin.util.Log
import com.appjoint2.plugin.visitor.AppJoint2ClassVisitor
import com.appjoint2.plugin.visitor.ApplicationClassVisitor
import com.appjoint2.plugin.visitor.FindClassVisitor
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import javax.inject.Inject

/**
 * @Author:JustGank
 * */
abstract class AppJoint2ClassTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val dirs: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Classpath
    abstract val bootClasspath: ListProperty<RegularFile>

    @get:CompileClasspath
    abstract var classpath: FileCollection

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    private var subProjectPath = HashSet<String>()
    private var appBuildDir = ""

    @TaskAction
    fun taskAction() {
        Log.i("AppJoint2ClassTask Start!")
        var time = System.currentTimeMillis()

        val parentProject = project.parent
        if (parentProject == null) {
            Log.w("AppJoint2ClassTask parentProject is null , AppJoint2 do nothing!")
        } else {
            appBuildDir = project.buildDir.path
            parentProject.subprojects.forEach {
                if (it.buildDir.path != appBuildDir)
                    subProjectPath.add(it.buildDir.path.also { Log.i("AppJoint2ClassTask subprojects buildDir:$it") })
            }

            findClass()
            Log.i("AppJoint2ClassTask find class custom : ${System.currentTimeMillis() - time}ms")
            time = System.currentTimeMillis()



            val appJointClass = "${ClassInfoRecord.ASM_APPJOINT_CLASS_PATH}.class"
            Log.i("AppJoint2ClassTask find Main AppSpec Class:${ClassInfoRecord.appSpecClass} , AppJoint Class :$appJointClass")

            writeClass(ClassInfoRecord.appSpecClass,appJointClass)
            Log.i("AppJoint2ClassTask write class custom : ${System.currentTimeMillis() - time}ms")
        }

    }

    private fun findClass() {
        jars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            if (checkTargetIsBelongSubproject(jarFile.name)) {
                Log.d("AppJoint2ClassTask find module jar : ${jarFile.name} ")
                jarFile.entries().iterator().forEach { jarEntry ->
                    if (jarEntry.isDirectory.not() && jarEntry.name.endsWith(".class")) {
                        jarFile.getInputStream(jarEntry).use { inputStream ->
                            findClassByVisit(inputStream, jarEntry.name.replace(".class",""))
                        }
                    }
                }
            }
        }
        dirs.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile && file.path.endsWith(".class")) {
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                    Log.d("AppJoint2ClassTask directory asFile relativePath : ${relativePath}")
                    file.inputStream().use { inputStream ->
                        findClassByVisit(inputStream, relativePath.replace(".class",""))
                    }
                }
            }
        }
    }


    private fun checkTargetIsBelongSubproject(path: String): Boolean {
        subProjectPath.forEach {
            if (path.startsWith(it))
                return true
        }
        return false
    }

    fun findClassByVisit(inputStream: InputStream, className: String) {
        val classReader = ClassReader(inputStream)
        classReader.accept(FindClassVisitor(className, classReader.interfaces), 0)
    }

    fun writeClass(mainAppSpecClass:String,appJointClass:String){
        JarOutputStream(BufferedOutputStream(FileOutputStream(output.get().asFile))).use { jarOutput ->

            jars.get().forEach { file ->
                val jarFile = JarFile(file.asFile)
                jarFile.entries().iterator().forEach { jarEntry ->

                    if (jarEntry.isDirectory.not() && (
                                jarEntry.name.equals(mainAppSpecClass)
                                        || jarEntry.name.equals(appJointClass))
                    ) {
                        jarOutput.putNextEntry(JarEntry(jarEntry.name))
                        jarFile.getInputStream(jarEntry).use { inputStream ->
                            if (jarEntry.name.equals(mainAppSpecClass)) {
                                Log.i("AppJointClassTask find mainAppSpecClass:$mainAppSpecClass in jars.")
                                jarOutput.write(asmVisitApplication(inputStream).toByteArray())
                            } else if (jarEntry.name.equals(appJointClass)) {
                                Log.i("AppJointClassTask find appJointClass:$appJointClass in jars.")
                                jarOutput.write(asmVisitAppJoint(inputStream).toByteArray())
                            }
                        }
                    } else {
                        kotlin.runCatching {
                            jarOutput.putNextEntry(JarEntry(jarEntry.name))
                            jarFile.getInputStream(jarEntry).use {
                                it.copyTo(jarOutput)
                            }
                        }
                    }
                    jarOutput.closeEntry()
                }

                jarFile.close()

            }

            dirs.get().forEach { directory ->
                directory.asFile.walk().forEach { file ->
                    if (file.isFile) {
                        val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                        val entryName = relativePath.replace(File.separatorChar, '/')
                        jarOutput.putNextEntry(JarEntry(entryName))
                        if (entryName == mainAppSpecClass) {
                            Log.i("AppJointClassTask find mainAppSpecClass:$mainAppSpecClass in dirs.")
                            file.inputStream().use { inputStream ->
                                val bytes = asmVisitApplication(inputStream).toByteArray()
                                jarOutput.write(bytes)
                            }
                        } else if (entryName == appJointClass) {
                            Log.i("AppJointClassTask find appJointClass:$appJointClass in dirs.")
                            file.inputStream().use { inputStream ->
                                jarOutput.write(asmVisitAppJoint(inputStream).toByteArray())
                            }
                        } else {
                            file.inputStream().use { inputStream ->
                                inputStream.copyTo(jarOutput)
                            }
                        }
                        jarOutput.closeEntry()
                    }
                }
            }
        }
    }

    fun asmVisitApplication(inputStream: InputStream): ClassWriter {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, 0)
        val classVisitor = ApplicationClassVisitor(classWriter)
        classReader.accept(classVisitor, 0)
        return classWriter
    }

    fun asmVisitAppJoint(inputStream: InputStream): ClassWriter {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, 0)
        val classVisitor = AppJoint2ClassVisitor(classWriter)
        classReader.accept(classVisitor, 0)
        return classWriter
    }
}
      