package services

import java.util.*

class ConfigReaderService {
    companion object {
        @JvmStatic
        fun getConfig(): Map<String, String> {
            val mapOfProperties = mutableMapOf<String, String>()
            javaClass.classLoader.getResourceAsStream("application.properties").use {
                Properties().apply { load(it) }
            }.forEach { (k, v) -> mapOfProperties.put(k.toString(), v.toString()) }
            return mapOfProperties
        }
    }
}