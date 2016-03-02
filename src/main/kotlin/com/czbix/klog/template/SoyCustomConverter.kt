package com.czbix.klog.template

import com.czbix.klog.database.dao.PostDao
import com.czbix.klog.database.dao.UserDao
import com.google.template.soy.data.SoyCustomValueConverter
import com.google.template.soy.data.SoyMapData
import com.google.template.soy.data.SoyValueConverter
import com.google.template.soy.data.SoyValueProvider
import java.lang.reflect.Method

class SoyCustomConverter : SoyCustomValueConverter {
    private val classFieldsCache = hashMapOf<Class<*>, List<Method>>()

    override fun convert(converter: SoyValueConverter, obj: Any): SoyValueProvider? {
        return when (obj) {
            is PostDao.Post -> SoyMapData(getFieldMap(obj))
            is UserDao.User -> SoyMapData(getFieldMap(obj))
            else -> null
        }
    }

    private inline fun <reified T: Any> getFieldMap(obj: T): Map<String, Any?> {
        val cls = T::class.java
        val fields = classFieldsCache.getOrPut(cls, {
            cls.declaredMethods.filter { it.name.startsWith("get") && it.parameters.isEmpty() }
        })
        if (fields.isEmpty()) {
            return mapOf()
        }

        val map = mutableMapOf<String, Any?>()
        fields.forEach {
            val name = it.name.substringAfter("get").decapitalize()
            val value = it.invoke(obj)

            map[name] = value
        }

        return map
    }
}
