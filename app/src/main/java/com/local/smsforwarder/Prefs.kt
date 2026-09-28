package com.local.smsforwarder

import android.content.Context

object Prefs {
    private const val FILE = "sms_forwarder"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_TARGET = "target_number"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun getTargetNumber(context: Context): String =
        prefs(context).getString(KEY_TARGET, "")?.trim().orEmpty()

    fun setTargetNumber(context: Context, number: String) {
        prefs(context).edit().putString(KEY_TARGET, number.trim()).apply()
    }
}
