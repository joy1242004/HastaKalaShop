package com.hastakala.shop.util

import android.content.Context

object UserPrefs {
    private const val FILE = "hastakala_prefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_RECOVERY_PIN = "recovery_pin"
    private const val KEY_SHOP_PHONE = "shop_phone"
    private const val KEY_DARK_MODE = "dark_mode"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    // Defaults for fresh install
    private const val DEFAULT_USER = "admin"
    private const val DEFAULT_PASS = "1234"
    private const val DEFAULT_PIN = "0000"

    fun isLoggedIn(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_LOGGED_IN, false)

    fun login(ctx: Context, username: String, password: String): Boolean {
        val savedUser = prefs(ctx).getString(KEY_USERNAME, DEFAULT_USER)
        val savedPass = prefs(ctx).getString(KEY_PASSWORD, DEFAULT_PASS)
        
        if (username.trim() == savedUser && password == savedPass) {
            prefs(ctx).edit().putBoolean(KEY_LOGGED_IN, true).apply()
            return true
        }
        return false
    }

    fun register(ctx: Context, username: String, pass: String): Boolean {
        prefs(ctx).edit()
            .putString(KEY_USERNAME, username.trim())
            .putString(KEY_PASSWORD, pass)
            .apply()
        return true
    }

    fun verifyRecoveryPin(ctx: Context, pin: String): Boolean {
        val savedPin = prefs(ctx).getString(KEY_RECOVERY_PIN, DEFAULT_PIN)
        return pin == savedPin
    }

    fun resetPassword(ctx: Context, newPass: String) {
        prefs(ctx).edit().putString(KEY_PASSWORD, newPass).apply()
    }

    fun logout(ctx: Context) {
        prefs(ctx).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun username(ctx: Context): String = prefs(ctx).getString(KEY_USERNAME, "Artisan") ?: "Artisan"

    fun shopPhone(ctx: Context): String = prefs(ctx).getString(KEY_SHOP_PHONE, "") ?: ""
    fun setShopPhone(ctx: Context, phone: String) {
        prefs(ctx).edit().putString(KEY_SHOP_PHONE, phone).apply()
    }

    fun isDarkMode(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_DARK_MODE, false)
    fun setDarkMode(ctx: Context, isDark: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }
}
