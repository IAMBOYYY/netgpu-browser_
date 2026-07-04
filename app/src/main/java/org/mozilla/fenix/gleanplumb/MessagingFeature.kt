/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.gleanplumb

import mozilla.components.support.base.feature.LifecycleAwareFeature
import com.netgpu.browser.components.AppStore
import com.netgpu.browser.components.appstate.AppAction.MessagingAction
import com.netgpu.browser.nimbus.MessageSurfaceId

/**
 * A message observer that updates the provided.
 */
class MessagingFeature(val appStore: AppStore) : LifecycleAwareFeature {

    override fun start() {
        appStore.dispatch(MessagingAction.Evaluate(MessageSurfaceId.HOMESCREEN))
    }

    override fun stop() = Unit
}
