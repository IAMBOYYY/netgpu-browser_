/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.recentvisits.view

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import mozilla.components.service.glean.private.NoExtras
import com.netgpu.browser.GleanMetrics.History
import com.netgpu.browser.GleanMetrics.RecentlyVisitedHomepage
import com.netgpu.browser.R
import com.netgpu.browser.components.components
import com.netgpu.browser.compose.ComposeViewHolder
import com.netgpu.browser.home.recentvisits.RecentlyVisitedItem
import com.netgpu.browser.home.recentvisits.RecentlyVisitedItem.RecentHistoryGroup
import com.netgpu.browser.home.recentvisits.RecentlyVisitedItem.RecentHistoryHighlight
import com.netgpu.browser.home.recentvisits.interactor.RecentVisitsInteractor
import com.netgpu.browser.wallpapers.WallpaperState

/**
 * View holder for [RecentlyVisitedItem]s.
 *
 * @param composeView [ComposeView] which will be populated with Jetpack Compose UI content.
 * @property interactor [RecentVisitsInteractor] which will have delegated to all user interactions.
 */
class RecentlyVisitedViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: RecentVisitsInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    init {
        val horizontalPadding =
            composeView.resources.getDimensionPixelSize(R.dimen.home_item_horizontal_margin)
        composeView.setPadding(horizontalPadding, 0, horizontalPadding, 0)
    }

    @Composable
    override fun Content() {
        val recentVisits = components.appStore
            .observeAsComposableState { state -> state.recentHistory }
        val wallpaperState = components.appStore
            .observeAsComposableState { state -> state.wallpaperState }.value ?: WallpaperState.default

        RecentlyVisited(
            recentVisits = recentVisits.value ?: emptyList(),
            menuItems = listOfNotNull(
                RecentVisitMenuItem(
                    title = stringResource(R.string.recently_visited_menu_item_remove),
                    onClick = { visit ->
                        when (visit) {
                            is RecentHistoryGroup -> interactor.onRemoveRecentHistoryGroup(visit.title)
                            is RecentHistoryHighlight -> interactor.onRemoveRecentHistoryHighlight(
                                visit.url,
                            )
                        }
                    },
                ),
            ),
            backgroundColor = wallpaperState.wallpaperCardColor,
            onRecentVisitClick = { recentlyVisitedItem, pageNumber ->
                when (recentlyVisitedItem) {
                    is RecentHistoryHighlight -> {
                        RecentlyVisitedHomepage.historyHighlightOpened.record(NoExtras())
                        interactor.onRecentHistoryHighlightClicked(recentlyVisitedItem)
                    }
                    is RecentHistoryGroup -> {
                        RecentlyVisitedHomepage.searchGroupOpened.record(NoExtras())
                        History.recentSearchesTapped.record(
                            History.RecentSearchesTappedExtra(
                                pageNumber.toString(),
                            ),
                        )
                        interactor.onRecentHistoryGroupClicked(recentlyVisitedItem)
                    }
                }
            },
        )
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
