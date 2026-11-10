package com.example.core.ai.util

object AIUtils {
    /**
     * Helper to clean up formatting or raw text outputs if necessary.
     */
    fun sanitizeOutput(input: String): String {
        return input.trim()
    }
}
