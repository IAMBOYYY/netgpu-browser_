/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.fenix.netgpu

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NetGpuViewerActivity : AppCompatActivity() {

    private val requestId = UUID.randomUUID().toString()
    private val partialFrames = HashMap<String, FrameAssembly>()

    private lateinit var imageView: ImageView
    private lateinit var statusView: TextView

    private var sessionId: String? = null
    private var stopSent = false
    private var resolved = false
    private var lastShownTs = 0L
    private var frameCount = 0

    private val url: String by lazy { intent.getStringExtra(EXTRA_URL).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (url.isBlank()) {
            finish()
            return
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(buildContentView())
        setStatus("Connecting to NetGPU...")

        NetGpuSocketClient.connect()
        NetGpuSocketClient.eventListener = { event, payload ->
            runOnUiThread {
                if (!isDestroyed) handleEvent(event, payload)
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeViewer()
                }
            },
        )

        lifecycleScope.launch {
            val deadline = System.currentTimeMillis() + LINK_TIMEOUT_MS
            while (System.currentTimeMillis() < deadline) {
                if (NetGpuSocketClient.isLinked()) {
                    NetGpuSocketClient.emitRequest(requestId, url)
                    return@launch
                }
                delay(LINK_POLL_MS)
            }
        }

        lifecycleScope.launch {
            delay(SESSION_TIMEOUT_MS)
            if (!resolved) {
                fallback("NetGPU unavailable - loading normally")
            }
        }
    }

    private fun buildContentView(): FrameLayout {
        val root = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }

        imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        root.addView(imageView)

        statusView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundColor(Color.argb(170, 0, 0, 0))
            setPadding(dp(12), dp(8), dp(12), dp(8))
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP,
            ).apply { setMargins(dp(8), dp(8), dp(56), 0) }
        }
        root.addView(statusView)

        val closeButton = TextView(this).apply {
            text = "✕"
            setTextColor(Color.WHITE)
            textSize = 22f
            gravity = Gravity.CENTER
            setBackgroundColor(Color.argb(170, 0, 0, 0))
            setOnClickListener { closeViewer() }
            layoutParams = FrameLayout.LayoutParams(dp(44), dp(44), Gravity.TOP or Gravity.END)
                .apply {
                    topMargin = dp(4)
                    rightMargin = dp(4)
                }
        }
        root.addView(closeButton)

        return root
    }

    private fun handleEvent(event: String, payload: JSONObject) {
        when (event) {
            "browser_no_donor" -> {
                if (payload.optString("requestId") != requestId) return
                fallback("No donor online - loading normally")
            }

            "browser_session_ready" -> {
                if (payload.optString("requestId") != requestId) return
                if (resolved) return
                sessionId = payload.optString("sessionId")
                resolved = true
                val donor = payload.optJSONObject("donor")
                val device = donor?.optString("device").orEmpty().ifBlank { "donor" }
                val gpuPower = donor?.optInt("gpuPower", 0) ?: 0
                setStatus("Donor found: $device ($gpuPower%) - loading page on donor...")
            }

            "browser_status" -> {
                if (!matchesSession(payload)) return
                val state = payload.optString("state")
                val msg = payload.optString("msg")
                setStatus(if (msg.isBlank()) state else "$state $msg")
            }

            "browser_frame_chunk" -> {
                if (!matchesSession(payload)) return
                handleFrameChunk(payload)
            }

            "browser_session_ended" -> {
                if (!matchesSession(payload)) return
                resolved = true
                setStatus("Session ended: ${payload.optString("reason")}")
            }
        }
    }

    private fun matchesSession(payload: JSONObject): Boolean {
        val current = sessionId ?: return false
        return payload.optString("sessionId") == current
    }

    private fun handleFrameChunk(payload: JSONObject) {
        val frameId = payload.opt("frameId")?.toString() ?: return
        val total = payload.optInt("total", 1)
        if (total <= 0) return
        val seq = payload.optInt("seq", 0)
        val ts = payload.optLong("ts", 0L)
        val data = payload.optString("data")
        if (data.isEmpty()) return
        if (ts > 0 && lastShownTs > 0 && ts < lastShownTs) return

        val assembly = partialFrames.getOrPut(frameId) { FrameAssembly(total, ts) }
        if (ts > assembly.ts) assembly.ts = ts
        if (!assembly.add(seq, data)) {
            if (partialFrames.size > MAX_PARTIAL_FRAMES) partialFrames.clear()
            return
        }

        partialFrames.remove(frameId)
        if (partialFrames.size > MAX_PARTIAL_FRAMES) partialFrames.clear()

        showFrame(assembly.chunks.joinToString("") { it ?: "" }, assembly.ts)
    }

    private fun showFrame(base64Data: String, ts: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val bytes = try {
                Base64.decode(base64Data, Base64.DEFAULT)
            } catch (_: IllegalArgumentException) {
                return@launch
            }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@launch
            val kb = bytes.size / 1024

            withContext(Dispatchers.Main) {
                if (isDestroyed || isFinishing) return@withContext
                if (ts > 0 && lastShownTs > 0 && ts < lastShownTs) return@withContext
                if (ts > lastShownTs) lastShownTs = ts
                frameCount++
                imageView.setImageBitmap(bitmap)
                setStatus("Rendered on donor device - frame $frameCount - $kb KB")
            }
        }
    }

    private fun fallback(message: String) {
        if (resolved) return
        resolved = true
        setStatus(message)
        NetGpuBridge.runFallback()
        finish()
    }

    private fun setStatus(text: String) {
        if (::statusView.isInitialized) statusView.text = text
    }

    private fun closeViewer() {
        stopSession()
        finish()
    }

    private fun stopSession() {
        resolved = true
        val current = sessionId
        if (current != null && !stopSent) {
            stopSent = true
            NetGpuSocketClient.emitStop(current)
        }
        NetGpuSocketClient.eventListener = null
        sessionId = null
    }

    override fun onDestroy() {
        stopSession()
        super.onDestroy()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private class FrameAssembly(val total: Int, var ts: Long) {
        val chunks = arrayOfNulls<String>(total)
        private var received = 0

        fun add(seq: Int, data: String): Boolean {
            if (seq !in 0 until total || chunks[seq] != null) return received == total
            chunks[seq] = data
            received++
            return received == total
        }
    }

    companion object {
        private const val EXTRA_URL = "com.netgpu.browser.fenix.netgpu.EXTRA_URL"
        private const val LINK_POLL_MS = 200L
        private const val LINK_TIMEOUT_MS = 5000L
        private const val SESSION_TIMEOUT_MS = 12000L
        private const val MAX_PARTIAL_FRAMES = 8

        fun start(context: Context, url: String) {
            context.startActivity(
                Intent(context, NetGpuViewerActivity::class.java).putExtra(EXTRA_URL, url),
            )
        }
    }
}
