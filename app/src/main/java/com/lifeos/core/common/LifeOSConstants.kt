package com.lifeos.core.common

object LifeOSConstants {
    const val APP_NAME = "LifeOS"
    const val APP_VERSION = "2.4.0"
    
    const val DEFAULT_POMODORO_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_MINUTES = 5
    const val DEFAULT_LONG_BREAK_MINUTES = 15
    
    const val MAX_OFFLINE_STORAGE_DAYS = 30
    const val DEFAULT_LIFE_SCORE_BASE = 85
    
    object Database {
        const val NAME = "lifeos_master_database.db"
        const val VERSION = 1
    }
    
    object Preferences {
        const val PREF_NAME = "lifeos_secure_prefs"
        const val KEY_USER_TOKEN = "key_user_token"
        const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        const val KEY_DARK_THEME = "key_dark_theme"
        const val KEY_USER_NAME = "key_user_name"
    }
}
