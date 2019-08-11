package com.better.alarm.presenter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.better.alarm.R
import com.better.alarm.alert.AlarmAlertFullScreen

class DynamicThemeHandler(context: Context) {
    private val themeKey = "theme"
    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        when (sp.getString(themeKey, "dark")) {
            "light", "dark", "green", "dark_purple" -> {
            }
            else -> {
                sp.edit().putString(themeKey, "dark").apply()
            }
        }
    }

    fun defaultTheme(): Int = when (preference()) {
        "light" -> R.style.DefaultLightTheme
        "dark" -> R.style.DefaultDarkTheme
        "green" -> R.style.DefaultLightGreenRedTheme
        "dark_purple" -> R.style.DefaultDarkPurpleTheme
        else -> R.style.DefaultDarkTheme
    }

    private fun preference(): String = sp.getString(themeKey, "dark")

    fun getIdForName(name: String): Int = when {
        preference() == "light" && name == AlarmAlertFullScreen::class.java.name -> R.style.AlarmAlertFullScreenLightTheme
        preference() == "light" && name == TimePickerDialogFragment::class.java.name -> R.style.TimePickerDialogFragmentLight
        preference() == "dark" && name == AlarmAlertFullScreen::class.java.name -> R.style.AlarmAlertFullScreenDarkTheme
        preference() == "dark" && name == TimePickerDialogFragment::class.java.name -> R.style.TimePickerDialogFragmentDark
        preference() == "green" && name == AlarmAlertFullScreen::class.java.name -> R.style.AlarmAlertFullScreenLightGreenRedTheme
        preference() == "green" && name == TimePickerDialogFragment::class.java.name -> R.style.TimePickerDialogFragmentLightGreenRed
        preference() == "dark_purple" && name == AlarmAlertFullScreen::class.java.name -> R.style.AlarmAlertFullScreenDarkPurpleTheme
        preference() == "dark_purple" && name == TimePickerDialogFragment::class.java.name -> R.style.TimePickerDialogFragmentDarkPurple
        else -> defaultTheme()
    }
}
