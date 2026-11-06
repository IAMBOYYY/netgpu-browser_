/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.fenix.netgpu

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val URL_CHANGE_DEBOUNCE_MS = 300L

object NetGpuBridge {

    private var isInitialized = false
    private var urlChangeJob: Job? = null
    private var bridgeScope: CoroutineScope? = null

    fun initialize(context: Context, coroutineScope: CoroutineScope) {
        if (isInitialized) return
        bridgeScope = coroutineScope
        NetGpuSocketClient.initialize(context, coroutineScope)
        isInitialized = true
    }

    fun onSearchIntent(query: String, engine: String?) {
        if (!isInitialized) return
        NetGpuSocketClient.connect()
        NetGpuSocketClient.emitSearch(query, engine)
    }

    fun onUrlChanged(url: String, title: String?) {
        if (!isInitialized || bridgeScope == null) return
        NetGpuSocketClient.connect()

        urlChangeJob?.cancel()
        urlChangeJob = bridgeScope!!.launch(Dispatchers.IO) {
            delay(URL_CHANGE_DEBOUNCE_MS)
            NetGpuSocketClient.emitUrlChange(url, title)
        }
    }
}
