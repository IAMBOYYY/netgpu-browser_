/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.tabstray.ext

import android.view.View
import com.netgpu.browser.R
import com.netgpu.browser.components.NetGpuBrowserSnackbar
import com.netgpu.browser.tabstray.TabsTrayFragment.Companion.ELEVATION

internal fun NetGpuBrowserSnackbar.collectionMessage(
    tabSize: Int,
    isNewCollection: Boolean = false,
): NetGpuBrowserSnackbar {
    val stringRes = when {
        isNewCollection -> {
            R.string.create_collection_tabs_saved_new_collection
        }
        tabSize > 1 -> {
            R.string.create_collection_tabs_saved
        }
        else -> {
            R.string.create_collection_tab_saved
        }
    }
    setText(context.getString(stringRes))
    return this
}

internal fun NetGpuBrowserSnackbar.bookmarkMessage(
    tabSize: Int,
): NetGpuBrowserSnackbar {
    val stringRes = when {
        tabSize > 1 -> {
            R.string.snackbar_message_bookmarks_saved
        }
        else -> {
            R.string.bookmark_saved_snackbar
        }
    }
    setText(context.getString(stringRes))
    return this
}

internal inline fun NetGpuBrowserSnackbar.anchorWithAction(
    anchor: View?,
    crossinline action: () -> Unit,
): NetGpuBrowserSnackbar {
    anchorView = anchor
    view.elevation = ELEVATION

    setAction(context.getString(R.string.create_collection_view)) {
        action.invoke()
    }

    return this
}

internal fun NetGpuBrowserSnackbar.Companion.make(view: View) = make(
    duration = LENGTH_LONG,
    isDisplayedWithBrowserToolbar = true,
    view = view,
)
