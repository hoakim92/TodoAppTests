package utils

import kotlin.random.Random

class RandomUtils {
    companion object {
        fun getRandomString(length: Int = 8) : String {
            val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return List(length) { charset.random() }.joinToString("")
        }

        fun getRandomPositiveInt() = Random.nextInt(0, Int.MAX_VALUE)
    }
}