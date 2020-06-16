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
    private const val AVATAR = "AVATAR"
    private const val ABOUT = "ABOUT"
    private const val GROUP = "GROUP"
    private const val APP_THEME = "APP_THEME"
    private const val SCHEDULE = "SCHEDULE"

    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveAppTheme(theme: Int) {
        putValue(APP_THEME to theme)
    }

    fun getAppTheme() : Int = prefs.getInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_NO)

    fun getProfile(): Profile = Profile(
        prefs.getString(FIRST_NAME, "")!!,
        prefs.getString(LAST_NAME, "")!!,
        prefs.getString(NICK_NAME, "")!!,
        prefs.getString(AVATAR, "")!!,
        prefs.getString(ABOUT, "")!!,
        prefs.getString(GROUP, "")!!
    )

    fun getProfileName(): String = prefs.getString(FIRST_NAME, "")!! + " " + prefs.getString(LAST_NAME, "")!!

    fun saveProfile(profile: Profile) {
        with(profile)
        {
            putValue(FIRST_NAME to firstName)
            putValue(LAST_NAME to lastName)
            putValue(NICK_NAME to nickName)
            putValue(AVATAR to avatar)
            putValue(ABOUT to about)
            putValue(GROUP to group)
        }
    }

    fun clearProfilePreferences(){
        putValue(FIRST_NAME to "")
        putValue(LAST_NAME to "")
        putValue(NICK_NAME to "")
        putValue(AVATAR to "")
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

    fun saveSchedule(schedule: String){
        putValue(SCHEDULE to schedule)
    }

    fun getSchedule(): String = prefs.getString(SCHEDULE, "")!!
}