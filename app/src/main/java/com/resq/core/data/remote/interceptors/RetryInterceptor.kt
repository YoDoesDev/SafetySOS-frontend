package com.resq.core.data.remote.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var exception: IOException? = null

        for (attempt in 1..maxRetries) {
            try {
                // Execute request
                val response = chain.proceed(request)
                
                // If HTTP request reaches server (even if 4xx/5xx), return response to caller
                return response
            } catch (e: IOException) {
                exception = e
                
                if (attempt < maxRetries) {
                    try {
                        // Sleep briefly between network retries
                        Thread.sleep(500L * attempt)
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
            }
        }
        
        throw exception ?: IOException("Network request failed after $maxRetries attempts")
    }
}
