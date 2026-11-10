package com.resq.core.data.local.preferences

import android.content.Context
import java.util.UUID

object DeviceIdManager {
    private const val PREF_NAME = "resq_device_prefs"
    private const val KEY_DEVICE_UUID = "device_uuid"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        let {
            var uuid = prefs.getString(KEY_DEVICE_UUID, null)
            if (uuid == null) {
                uuid = UUID.randomUUID().toString()
                prefs.edit().putString(KEY_DEVICE_UUID, uuid).apply()
            }
            return uuid
        }
    }
}
