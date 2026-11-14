package com.resq.core.data.remote.socket

import com.resq.core.data.local.preferences.TokenManager
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/** App-wide Socket.IO connection to the authenticated backend. */
@Singleton
class SocketManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    companion object {
        // Keep this host aligned with NetworkModule's Retrofit base URL.
        private const val SOCKET_URL = "https://safetysos-api.onrender.com"
    }

    @Volatile
    private var socket: Socket? = null

    /** Connects using the latest access token stored by the auth/session flow. */
    @Synchronized
    fun connect() {
        // Keep a connecting or reconnecting Socket.IO instance; don't replace it
        // on every Activity start while its handshake is still in progress.
        if (socket != null) return

        val token = tokenManager.getAccessToken()
        if (token.isNullOrBlank()) return

        disconnectSocketOnly()

        try {
            val options = IO.Options.builder()
                .setAuth(mapOf("token" to token))
                .setReconnection(true)
                .setReconnectionAttempts(Int.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(30_000)
                .build()

            socket = IO.socket(SOCKET_URL, options).also { it.connect() }
        } catch (e: URISyntaxException) {
            throw IllegalStateException("Invalid Socket.IO server URL", e)
        }
    }

    /** Disconnects and drops listeners. Call when the user logs out. */
    @Synchronized
    fun disconnect() {
        disconnectSocketOnly()
    }

    /** Recreate the connection after the session flow has refreshed the access token. */
    @Synchronized
    fun reconnectWithLatestToken() {
        disconnectSocketOnly()
        connect()
    }

    fun isConnected(): Boolean = socket?.connected() == true

    /** Subscribe to a backend event, for example `new_message` or `link_update`. */
    fun on(event: String, listener: (Array<Any>) -> Unit) {
        socket?.on(event) { args -> listener(args) }
    }

    fun off(event: String) {
        socket?.off(event)
    }

    /** Emit an event without waiting for the server acknowledgement. */
    fun emit(event: String, payload: JSONObject = JSONObject()): Boolean {
        val current = socket ?: return false
        if (!current.connected()) return false
        current.emit(event, payload)
        return true
    }

    /** Emit an event and receive the backend's acknowledgement payload. */
    fun emitWithAck(
        event: String,
        payload: JSONObject = JSONObject(),
        onAck: (Array<Any>) -> Unit
    ): Boolean {
        val current = socket ?: return false
        if (!current.connected()) return false
        current.emit(event, payload, Ack { args -> onAck(args) })
        return true
    }

    private fun disconnectSocketOnly() {
        socket?.let { current ->
            current.disconnect()
            current.off()
            current.close()
        }
        socket = null
    }
}