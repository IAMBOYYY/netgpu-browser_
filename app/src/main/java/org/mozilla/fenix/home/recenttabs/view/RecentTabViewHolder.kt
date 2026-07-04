/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.recenttabs.view

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import com.netgpu.browser.R
import com.netgpu.browser.components.components
import com.netgpu.browser.compose.ComposeViewHolder
import com.netgpu.browser.home.recenttabs.interactor.RecentTabInteractor
import com.netgpu.browser.wallpapers.WallpaperState

/**
 * View holder for a recent tab item.
 *
 * @param composeView [ComposeView] which will be populated with Jetpack Compose UI content.
 * @param recentTabInteractor [RecentTabInteractor] which will have delegated to all user recent
 * tab interactions.
 * recent synced tab interactions.
 */
class RecentTabViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val recentTabInteractor: RecentTabInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    init {
        val horizontalPadding =
            composeView.resources.getDimensionPixelSize(R.dimen.home_item_horizontal_margin)
        composeView.setPadding(horizontalPadding, 0, horizontalPadding, 0)
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }

    @Composable
    override fun Content() {
        val recentTabs = components.appStore.observeAsComposableState { state -> state.recentTabs }
        val wallpaperState = components.appStore
            .observeAsComposableState { state -> state.wallpaperState }.value ?: WallpaperState.default

        RecentTabs(
            recentTabs = recentTabs.value ?: emptyList(),
            backgroundColor = wallpaperState.wallpaperCardColor,
            onRecentTabClick = { recentTabInteractor.onRecentTabClicked(it) },
            menuItems = listOf(
                RecentTabMenuItem(
                    title = stringResource(id = R.string.recent_tab_menu_item_remove),
                    onClick = { tab -> recentTabInteractor.onRemoveRecentTab(tab) },
                ),
            ),
        )
    }
}
