/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.topsites

import mozilla.components.feature.top.sites.TopSite
import mozilla.components.feature.top.sites.view.TopSitesView
import com.netgpu.browser.components.AppStore
import com.netgpu.browser.components.appstate.AppAction
import com.netgpu.browser.ext.sort
import com.netgpu.browser.utils.Settings

class DefaultTopSitesView(
    val appStore: AppStore,
    val settings: Settings,
) : TopSitesView {

    override fun displayTopSites(topSites: List<TopSite>) {
        appStore.dispatch(
            AppAction.TopSitesChange(
                if (!settings.showContileFeature) {
                    topSites
                } else {
                    topSites.sort()
                },
            ),
        )
    }
}
