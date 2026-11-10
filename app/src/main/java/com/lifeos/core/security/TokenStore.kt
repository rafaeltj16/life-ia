package com.lifeos.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.lifeos.core.common.LifeOSConstants

interface TokenStore {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun isUserLoggedIn(): Boolean
}

class SecureTokenStore(context: Context) : TokenStore {
    private val prefs: SharedPreferences = try {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            LifeOSConstants.Preferences.PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences(LifeOSConstants.Preferences.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun saveToken(token: String) {
        prefs.edit().putString(LifeOSConstants.Preferences.KEY_USER_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(LifeOSConstants.Preferences.KEY_USER_TOKEN, null)
    }

    override fun clearToken() {
        prefs.edit().remove(LifeOSConstants.Preferences.KEY_USER_TOKEN).apply()
    }

    override fun isUserLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
