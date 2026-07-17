/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.browser

import android.view.View
import androidx.annotation.StringRes
import mozilla.components.feature.contextmenu.ContextMenuCandidate
import com.netgpu.browser.components.NetGpuBrowserSnackbar

class NetGpuBrowserSnackbarDelegate(private val view: View) : ContextMenuCandidate.SnackbarDelegate {

    override fun show(
        snackBarParentView: View,
        @StringRes text: Int,
        duration: Int,
        @StringRes action: Int,
        listener: ((v: View) -> Unit)?,
    ) {
        if (listener != null && action != 0) {
            NetGpuBrowserSnackbar.make(
                view = view,
                duration = NetGpuBrowserSnackbar.LENGTH_SHORT,
                isDisplayedWithBrowserToolbar = true,
            )
                .setText(view.context.getString(text))
                .setAction(view.context.getString(action)) { listener.invoke(view) }
                .show()
        } else {
            NetGpuBrowserSnackbar.make(
                view,
                duration = NetGpuBrowserSnackbar.LENGTH_SHORT,
                isDisplayedWithBrowserToolbar = true,
            )
                .setText(view.context.getString(text))
                .show()
        }
    }
}
