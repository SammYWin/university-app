package ru.bstu.diploma.repositories

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import ru.bstu.diploma.App
import ru.bstu.diploma.models.Profile

object PreferencesRepository
{
    private const val FIRST_NAME = "FIRST_NAME"
    private const val LAST_NAME = "LAST_NAME"
    private const val NICK_NAME = "NICK_NAME"
    private const val ABOUT = "ABOUT"
    private const val GROUP = "GROUP"
    private const val APP_THEME = "APP_THEME"

    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveAppTheme(theme: Int) {
        putValue(APP_THEME to theme)
    }

    fun getAppTheme() : Int = prefs.getInt(APP_THEME,AppCompatDelegate.MODE_NIGHT_NO)

    fun getProfile(): Profile = Profile(
        prefs.getString(FIRST_NAME, "")!!,
        prefs.getString(LAST_NAME, "")!!,
        prefs.getString(NICK_NAME, "")!!,
        prefs.getString(ABOUT, "")!!,
        prefs.getString(GROUP, "")!!
    )


    fun saveProfile(profile: Profile) {
        with(profile)
        {
            putValue(FIRST_NAME to firstName)
            putValue(LAST_NAME to lastName)
            putValue(NICK_NAME to nickName)
            putValue(ABOUT to about)
            putValue(GROUP to group)
        }
    }

    fun clearPreferences(){
        putValue(FIRST_NAME to "")
        putValue(LAST_NAME to "")
        putValue(NICK_NAME to "")
        putValue(ABOUT to "")
        putValue(GROUP to "")
    }

    private fun putValue(pair: Pair<String, Any>) = with(prefs.edit()) {
        val key = pair.first
        val value = pair.second

        when(value)
        {
            is String->putString(key, value)
            is Int->putInt(key, value)
            is Boolean->putBoolean(key, value)
            is Long->putLong(key, value)
            is Float->putFloat(key, value)
            else-> error("Only primitive types can be stored in Shared Preferences")
        }
        apply()
    }
}