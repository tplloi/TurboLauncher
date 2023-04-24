package com.roy.turbo.launcher.settings

import android.content.Context
import android.content.SharedPreferences

object SettingsProvider {
    const val SETTINGS_KEY = "com.roy.turbo.launcher_preferences"
    const val SETTINGS_CHANGED = "settings_changed"
    const val SETTINGS_UI_HOMESCREEN_DEFAULT_SCREEN_ID = "ui_homescreen_default_screen_id"
    const val SETTINGS_UI_HOMESCREEN_SEARCH = "ui_homescreen_search"
    const val SETTINGS_UI_HOMESCREEN_HIDE_ICON_LABELS = "ui_homescreen_general_hide_icon_labels"
    const val SETTINGS_UI_HOMESCREEN_SCROLLING_TRANSITION_EFFECT =
        "ui_homescreen_scrolling_transition_effect"
    const val SETTINGS_UI_HOMESCREEN_SCROLLING_WALLPAPER_SCROLL =
        "ui_homescreen_scrolling_wallpaper_scroll"
    const val SETTINGS_UI_HOMESCREEN_SCROLLING_PAGE_OUTLINES =
        "ui_homescreen_scrolling_page_outlines"
    const val SETTINGS_UI_HOMESCREEN_SCROLLING_FADE_ADJACENT =
        "ui_homescreen_scrolling_fade_adjacent"
    const val SETTINGS_UI_DRAWER_SCROLLING_TRANSITION_EFFECT =
        "ui_drawer_scrolling_transition_effect"
    const val SETTINGS_UI_DRAWER_SCROLLING_FADE_ADJACENT = "ui_drawer_scrolling_fade_adjacent"
    const val SETTINGS_UI_DRAWER_REMOVE_HIDDEN_APPS_SHORTCUTS =
        "ui_drawer_remove_hidden_apps_shortcuts"
    const val SETTINGS_UI_DRAWER_REMOVE_HIDDEN_APPS_WIDGETS = "ui_drawer_remove_hidden_apps_widgets"
    const val SETTINGS_UI_DRAWER_HIDE_ICON_LABELS = "ui_drawer_hide_icon_labels"
    const val SETTINGS_UI_GENERAL_ICONS_LARGE = "ui_general_icons_large"
    const val SETTINGS_UI_DRAWER_SORT_MODE = "ui_drawer_sort_mode"

    @JvmStatic
    operator fun get(context: Context): SharedPreferences {
        return context.getSharedPreferences(SETTINGS_KEY, Context.MODE_MULTI_PROCESS)
    }

    @JvmStatic
    fun getIntCustomDefault(
        context: Context,
        key: String?,
        def: Int
    ): Int {
        return SettingsProvider[context].getInt(key, def)
    }

    fun getInt(
        context: Context,
        key: String?,
        resource: Int
    ): Int {
        return getIntCustomDefault(context, key, context.resources.getInteger(resource))
    }

    @JvmStatic
    fun getLongCustomDefault(
        context: Context,
        key: String?,
        def: Long
    ): Long {
        return SettingsProvider[context].getLong(key, def)
    }

    fun getLong(
        context: Context,
        key: String?,
        resource: Int
    ): Long {
        return getLongCustomDefault(context, key, context.resources.getInteger(resource).toLong())
    }

    fun getBooleanCustomDefault(
        context: Context,
        key: String?,
        def: Boolean
    ): Boolean {
        return SettingsProvider[context].getBoolean(key, def)
    }

    @JvmStatic
    fun getBoolean(
        context: Context,
        key: String?,
        resource: Int
    ): Boolean {
        return getBooleanCustomDefault(context, key, context.resources.getBoolean(resource))
    }

    fun getStringCustomDefault(
        context: Context,
        key: String?,
        def: String?
    ): String? {
        return SettingsProvider[context].getString(key, def)
    }

    @JvmStatic
    fun getString(
        context: Context,
        key: String?,
        resource: Int
    ): String? {
        return getStringCustomDefault(context, key, context.resources.getString(resource))
    }

    fun putString(
        context: Context,
        key: String?,
        value: String?
    ) {
        SettingsProvider[context].edit().putString(key, value).apply()
    }

    @JvmStatic
    fun putInt(
        context: Context,
        key: String?,
        value: Int
    ) {
        SettingsProvider[context].edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun getThemeIcons(context: Context): Boolean {
        val preferences = context.getSharedPreferences(SETTINGS_KEY, 0)
        return preferences.getBoolean("themeIcons", true)
    }

    @JvmStatic
    fun getThemeFont(context: Context): Boolean {
        val sp = context.getSharedPreferences(SETTINGS_KEY, 0)
        return sp.getBoolean("themeFont", true)
    }

    @JvmStatic
    fun getThemePackageName(
        context: Context,
        default_theme: String?
    ): String? {
        val preferences = context.getSharedPreferences(SETTINGS_KEY, 0)
        return preferences.getString("themePackageName", default_theme)
    }

    @JvmStatic
    fun setThemePackageName(
        context: Context,
        packageName: String?
    ) {
        val preferences = context.getSharedPreferences(SETTINGS_KEY, 0)
        val editor = preferences.edit()
        editor.putString("themePackageName", packageName)
        editor.apply()
    }
}
