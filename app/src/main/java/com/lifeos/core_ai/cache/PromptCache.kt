package com.lifeos.core_ai.cache

class PromptCache {

    private val cache =
        mutableMapOf<String, String>()

    fun get(
        key: String
    ): String? {
        return cache[key]
    }

    fun put(
        key: String,
        value: String
    ) {
        cache[key] = value
    }

}
