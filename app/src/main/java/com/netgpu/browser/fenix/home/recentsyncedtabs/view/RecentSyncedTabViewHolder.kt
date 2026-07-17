/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.recentsyncedtabs.view

import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import com.netgpu.browser.R
import com.netgpu.browser.components.components
import com.netgpu.browser.compose.ComposeViewHolder
import com.netgpu.browser.home.recentsyncedtabs.RecentSyncedTabState
import com.netgpu.browser.home.recentsyncedtabs.interactor.RecentSyncedTabInteractor
import com.netgpu.browser.theme.NetGpuBrowserTheme
import com.netgpu.browser.wallpapers.Wallpaper
import com.netgpu.browser.wallpapers.WallpaperState

/**
 * View holder for a recent synced tab item.
 *
 * @param composeView [ComposeView] which will be populated with Jetpack Compose UI content.
 * @param recentSyncedTabInteractor [RecentSyncedTabInteractor] which will have delegated to all
 * recent synced tab user interactions.
 */
class RecentSyncedTabViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val recentSyncedTabInteractor: RecentSyncedTabInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    init {
        val horizontalPadding =
            composeView.resources.getDimensionPixelSize(R.dimen.home_item_horizontal_margin)
        val verticalPadding =
            composeView.resources.getDimensionPixelSize(R.dimen.home_item_vertical_margin)
        composeView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, 0)
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }

    @Composable
    override fun Content() {
        val recentSyncedTabState = components.appStore.observeAsComposableState { state -> state.recentSyncedTabState }
        val wallpaperState = components.appStore
            .observeAsComposableState { state -> state.wallpaperState }.value ?: WallpaperState.default
        val isWallpaperNotDefault = !Wallpaper.nameIsDefault(wallpaperState.currentWallpaper.name)

        recentSyncedTabState.value?.let {
            val syncedTab = when (it) {
                RecentSyncedTabState.None,
                RecentSyncedTabState.Loading,
                -> null
                is RecentSyncedTabState.Success -> it.tabs.firstOrNull()
            }
            val buttonBackgroundColor = when {
                syncedTab != null && isWallpaperNotDefault -> NetGpuBrowserTheme.colors.layer1
                syncedTab != null -> NetGpuBrowserTheme.colors.actionSecondary
                else -> NetGpuBrowserTheme.colors.layer3
            }
            val buttonTextColor = when {
                wallpaperState.currentWallpaper.cardColorDark != null &&
                    isSystemInDarkTheme() -> NetGpuBrowserTheme.colors.textPrimary
                else -> NetGpuBrowserTheme.colors.textActionSecondary
            }

            RecentSyncedTab(
                tab = syncedTab,
                backgroundColor = wallpaperState.wallpaperCardColor,
                buttonBackgroundColor = buttonBackgroundColor,
                buttonTextColor = buttonTextColor,
                onRecentSyncedTabClick = recentSyncedTabInteractor::onRecentSyncedTabClicked,
                onSeeAllSyncedTabsButtonClick = recentSyncedTabInteractor::onSyncedTabShowAllClicked,
                onRemoveSyncedTab = recentSyncedTabInteractor::onRemovedRecentSyncedTab,
            )
        }
    }
}
