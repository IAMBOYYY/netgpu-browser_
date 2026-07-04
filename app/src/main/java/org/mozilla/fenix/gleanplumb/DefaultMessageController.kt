/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.gleanplumb

import android.content.Intent
import com.netgpu.browser.HomeActivity
import com.netgpu.browser.components.AppStore
import com.netgpu.browser.components.appstate.AppAction.MessagingAction.MessageClicked
import com.netgpu.browser.components.appstate.AppAction.MessagingAction.MessageDismissed

/**
 * Handles default interactions with the ui of GleanPlumb messages.
 */
class DefaultMessageController(
    private val appStore: AppStore,
    private val messagingController: NimbusMessagingController,
    private val homeActivity: HomeActivity,
) : MessageController {

    override fun onMessagePressed(message: Message) {
        val actionUri = messagingController.processMessageActionToUri(message)
        homeActivity.processIntent(Intent(Intent.ACTION_VIEW, actionUri))

        appStore.dispatch(MessageClicked(message))
    }

    override fun onMessageDismissed(message: Message) {
        appStore.dispatch(MessageDismissed(message))
    }
}
