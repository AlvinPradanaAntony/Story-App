package com.devcode.storyapp.ui.login

import android.content.Context
import com.devcode.storyapp.R

class Session(context: Context) {
    companion object {
        private const val TOKEN_KEY = "authToken"
    }

    private val loginSession =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        val editor = loginSession.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun passToken(): String? {
        return loginSession.getString(TOKEN_KEY, null)
    }

    fun logoutSession() {
        loginSession.edit().clear().apply()
    }
}