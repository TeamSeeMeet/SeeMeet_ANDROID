package org.seemeet.seemeet.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Secure.putString
import androidx.core.content.edit

object SeeMeetSharedPreference {
    lateinit var preferences: SharedPreferences

    private const val USER_ID = "USER_ID"
    private const val IS_LOGIN = "IS_LOGIN"
    private const val USER_FB = "USER_FB"
    private const val TOKEN = "TOKEN"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }


    fun setUserId(value : Int){
        preferences.edit{
            putInt(USER_ID, value)
        }
    }

    fun getUserId(): Int= preferences.getInt(USER_ID, -1)

    fun setUserFb(value : String){
        preferences.edit{
            putString(USER_ID, value)
        }
    }

    fun getUserFb(): String = preferences.getString(USER_FB, "") ?: ""

    fun setToken(value : String){
        preferences.edit{
            putString(TOKEN, value)
        }
    }

    fun getToken(): String = preferences.getString(TOKEN, "") ?: ""


    fun setLogin(value: Boolean) {
        preferences.edit{
            putBoolean(IS_LOGIN, value)
        }
    }

    fun getLogin(): Boolean = preferences.getBoolean(IS_LOGIN, false)


    fun clearStorage() {
        preferences.edit { clear() }
    }
}

