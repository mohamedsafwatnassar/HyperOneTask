package com.hyperone.newsapp.utils

import android.app.Activity
import android.app.Application
import com.google.gson.Gson
import com.hyperone.newsapp.authentication.model.UserModel
import javax.inject.Inject


const val SHARED_PREFERENCE_USER_DATA = "sharedPreferenceUserData"
const val SHARED_PREFERENCE_SETTINGS = "sharedPreferenceSettingsData"
const val SAVED_USER = "saved_user"
const val LOGIN_FLAG = "login_flag"

class SharedPreferenceManager @Inject constructor(
    private val context: Application
) {
    private val mSharedPrefUserData by lazy {
        context.getSharedPreferences(SHARED_PREFERENCE_USER_DATA, Activity.MODE_PRIVATE)
    }

    private val mSharedPrefSettings by lazy {
        context.getSharedPreferences(SHARED_PREFERENCE_SETTINGS, Activity.MODE_PRIVATE)
    }

    fun setLoggedInFLag(isLogged: Boolean) {
        mSharedPrefUserData.edit().putBoolean(LOGIN_FLAG, isLogged).apply()
    }

    fun getLoggedInFLag(): Boolean {
        return mSharedPrefUserData.getBoolean(LOGIN_FLAG, false)
    }

    fun saveUser(user: UserModel?) {
        try {
            val editor = mSharedPrefUserData!!.edit()
            val gson = Gson()
            val json = gson.toJson(user)
            editor.putString(SAVED_USER, json)
            editor.apply()
        } catch (e: Exception) {
            return
        }
    }

    fun getSavedUser(): UserModel? {
        return try {
            val gson = Gson()
            val json = mSharedPrefUserData!!
                .getString(SAVED_USER, null)
            gson.fromJson(json, UserModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
