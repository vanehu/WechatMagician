package com.gh0u1l5.wechatmagician.util

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.*
import net.dongliu.apk.parser.bean.DexClass
import java.lang.reflect.Field

// PackageUtil is a helper object for static analysis
object PackageUtil {

    // shadowCopy copy all the fields of the object obj into the object copy.
    fun shadowCopy(obj: Any, copy: Any, clazz: Class<*>? = obj.javaClass) {
        if (clazz == null) {
            return
        }
        shadowCopy(obj, copy, clazz.superclass)
        clazz.declaredFields.forEach {
            it.isAccessible = true
            it.set(copy, it.get(obj))
        }
    }

    // getClassName parses the standard class name of the given DexClass.
    private fun getClassName(clazz: DexClass): String {
        return clazz.classType
                .replace('/', '.') // replace delimiters
                .drop(1) // drop leading 'L'
                .dropLast(1) //drop trailing ';'
    }

    // findClassesFromPackage returns a list of all the classes contained in the given package.
    fun findClassesFromPackage(
            loader: ClassLoader, classes: Array<DexClass>, packageName: String, depth: Int = 0
    ): List<Class<*>> {
        return classes.filter predicate@ {
            if (depth == 0) {
                return@predicate it.packageName == packageName
            }
            val satisfyPrefix = it.packageName.startsWith(packageName)
            val satisfyDepth =
                    it.packageName.drop(packageName.length).count{it == '.'} == depth
            return@predicate satisfyPrefix && satisfyDepth
        }.map { findClass(getClassName(it), loader)!! }
    }

    // findFirstClassWithMethod finds the first class that have the given method from a list of classes.
    fun findFirstClassWithMethod(
            classes: List<Class<*>>, returnType: Class<*>?, methodName: String, vararg parameterTypes: Class<*>
    ): Class<*>? {
        val clazz = classes.firstOrNull {
            val method = findMethodExactIfExists(it, methodName, *parameterTypes)
            method != null && method.returnType == returnType ?: method.returnType
        }
        if (clazz == null) {
            XposedBridge.log("HOOK => Cannot find class with method $returnType $methodName($parameterTypes)")
        }
        return clazz
    }
    fun findFirstClassWithMethod(
            classes: List<Class<*>>, returnType: Class<*>?, vararg parameterTypes: Class<*>
    ): Class<*>? {
        val clazz = classes.firstOrNull {
            findMethodsByExactParameters(it, returnType, *parameterTypes).isNotEmpty()
        }
        if (clazz == null) {
            XposedBridge.log("HOOK => Cannot find class with method signature $returnType fun($parameterTypes)")
        }
        return clazz
    }

    // findFirstClassWithField finds the first class that have the given field type from a list of classes.
    fun findFirstClassWithField(classes: List<Class<*>>, fieldType: String): Class<*>? {
        val clazz = classes.firstOrNull { findFieldsWithType(it, fieldType).isNotEmpty() }
        if (clazz == null) {
            XposedBridge.log("HOOK => Cannot find class with field type $fieldType")
        }
        return clazz
    }

    // findClassesWithSuper finds the classes that have the given super class.
    fun findClassesWithSuper(classes: List<Class<*>>, superClass: Class<*>): List<Class<*>> {
        return classes.filter { it.superclass == superClass }
    }

    // findFieldsWithGenericType finds all the fields of the given type.
    fun findFieldsWithType(clazz: Class<*>?, typeName: String): List<Field> {
        return clazz?.declaredFields?.filter {
            it.type.name == typeName
        } ?: listOf()
    }

    // findFieldsWithGenericType finds all the fields of the given generic type.
    fun findFieldsWithGenericType(clazz: Class<*>?, genericTypeName: String): List<Field> {
        return clazz?.declaredFields?.filter {
            it.genericType.toString() == genericTypeName
        } ?: listOf()
    }
}
