/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.fenix.netgpu

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

object NetGpuSocketClient {

    private const val SERVER_URL = "wss://netgpu-test-server.onrender.com/socket.io/?EIO=4&transport=websocket"
    private const val PREFS_NAME = "netgpu_prefs"
    private const val KEY_BROWSER_ID = "browser_id"
    private const val TAG = "NetGpu"

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var isConnecting = false
    private var retryCount = 0
    private var browserId: String? = null
    private var client: OkHttpClient? = null
    private var scope: CoroutineScope? = null

    fun initialize(context: Context, coroutineScope: CoroutineScope) {
        scope = coroutineScope
        client = OkHttpClient.Builder()
            .pingInterval(0, TimeUnit.SECONDS)
            .build()
        browserId = getOrCreateBrowserId(context)
    }

    private fun getOrCreateBrowserId(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BROWSER_ID, "").takeIf { it.isNotBlank() } ?: run {
            val newId = "browser_${UUID.randomUUID()}"
            prefs.edit().putString(KEY_BROWSER_ID, newId).apply()
            newId
        }
    }

    fun connect() {
        if (isConnected || isConnecting || client == null || scope == null || browserId == null) {
            return
        }

        isConnecting = true
        Log.d(TAG, "Connecting to $SERVER_URL")

        scope!!.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(SERVER_URL).build()
                client!!.newWebSocket(request, SocketListener())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start WebSocket connection", e)
                scheduleReconnect()
            }
        }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting")
        webSocket?.close(1000, "Client disconnected")
        webSocket = null
        isConnected = false
        isConnecting = false
        retryCount = 0
    }

    fun emitSearch(query: String, searchEngine: String?) {
        if (!isConnected) {
            Log.w(TAG, "emitSearch: not connected, ignoring")
            return
        }
        val payload = JSONObject().apply {
            put("browserId", browserId)
            put("query", query)
            put("searchEngine", searchEngine ?: "")
            put("timestamp", System.currentTimeMillis())
        }
        sendEvent("browser_search", payload)
    }

    fun emitUrlChange(url: String, title: String?) {
        if (!isConnected) {
            Log.w(TAG, "emitUrlChange: not connected, ignoring")
            return
        }
        val payload = JSONObject().apply {
            put("browserId", browserId)
            put("url", url)
            put("title", title ?: "")
            put("timestamp", System.currentTimeMillis())
        }
        sendEvent("browser_url_change", payload)
    }

    private fun sendEvent(eventName: String, payload: JSONObject) {
        val message = "42[\"$eventName\",${payload.toString()}]"
        Log.d(TAG, "Emitting $eventName: $message")
        webSocket?.send(message)
    }

    private inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket opened")
            NetGpuSocketClient.webSocket = webSocket
            isConnecting = false
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Received: $text")
            when {
                text.startsWith("0") -> {
                    Log.d(TAG, "Received Socket.IO open (0), sending 40")
                    webSocket.send("40")
                }
                text == "2" -> {
                    Log.d(TAG, "Received ping (2), sending pong (3)")
                    webSocket.send("3")
                }
                text == "40" -> {
                    Log.d(TAG, "Socket.IO handshake complete (40)")
                    isConnected = true
                    retryCount = 0
                }
                else -> {
                    Log.d(TAG, "Unhandled message: $text")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
            Log.d(TAG, "Received binary message")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closing: $code $reason")
            webSocket.close(code, reason)
            isConnected = false
            isConnecting = false
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $code $reason")
            isConnected = false
            isConnecting = false
            if (code != 1000) {
                scheduleReconnect()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket failure", t)
            isConnected = false
            isConnecting = false
            scheduleReconnect()
        }
    }

    private fun scheduleReconnect() {
        scope?.launch(Dispatchers.IO) {
            retryCount++
            val delayMs = minOf(1000 * 2.0.pow(retryCount - 1), 30000).toLong()
            Log.d(TAG, "Scheduling reconnect attempt $retryCount in ${delayMs}ms")
            delay(delayMs)
            if (!isConnected && !isConnecting) {
                connect()
            }
        }
    }
}