package com.resq.core.data.remote.interceptors

import android.content.Context
import com.resq.core.data.local.preferences.DeviceIdManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val deviceId = DeviceIdManager.getDeviceId(context)

        val newRequest = originalRequest.newBuilder()
            .header("X-Device-Id", deviceId) // 👈 Attached to every API call
            // .header("Authorization", "Bearer $token") // Keep your existing token logic here
            .build()

        return chain.proceed(newRequest)
    }
}
