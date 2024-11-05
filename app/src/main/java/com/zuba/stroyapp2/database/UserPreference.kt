package com.zuba.stroyapp2.database

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import com.zuba.stroyapp2.model.UserModelPreference
import androidx.security.crypto.MasterKey
internal class UserPreference(context: Context) {
    private val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()

    private val masterKey = MasterKey.Builder(context).setKeyGenParameterSpec(spec).build()

    private val preference: SharedPreferences = EncryptedSharedPreferences.create(
        context, PREFS_NAME, masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setLogin(value: UserModelPreference) {
        val editor = preference.edit()
        editor.putString(USER_ID, value.userId)
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getLogin(): UserModelPreference{
        val userModelPreference= UserModelPreference()
        userModelPreference.userId = preference.getString(USER_ID, "")
        userModelPreference.name = preference.getString(NAME, "")
        userModelPreference.token = preference.getString(TOKEN, "")

        return userModelPreference
    }

    fun clearLogin() {
        val editor = preference.edit()
        editor.clear()
        editor.remove(USER_ID)
        editor.remove(NAME)
        editor.remove(TOKEN)
        editor.apply()
    }

    companion object {
        const val PREFS_NAME = "UserPreference"
        const val USER_ID = "id"
        const val NAME = "name"
        const val TOKEN = "token"
    }
}