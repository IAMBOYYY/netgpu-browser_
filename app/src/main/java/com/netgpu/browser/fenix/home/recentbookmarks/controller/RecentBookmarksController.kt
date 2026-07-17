/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.recentbookmarks.controller

import androidx.navigation.NavController
import mozilla.appservices.places.BookmarkRoot
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.EngineSession.LoadUrlFlags.Companion.ALLOW_JAVASCRIPT_URL
import com.netgpu.browser.BrowserDirection
import com.netgpu.browser.GleanMetrics.RecentBookmarks
import com.netgpu.browser.HomeActivity
import com.netgpu.browser.components.AppStore
import com.netgpu.browser.components.appstate.AppAction
import com.netgpu.browser.home.HomeFragmentDirections
import com.netgpu.browser.home.recentbookmarks.RecentBookmark
import com.netgpu.browser.home.recentbookmarks.interactor.RecentBookmarksInteractor

/**
 * An interface that handles the view manipulation of the recently saved bookmarks on the
 * Home screen.
 */
interface RecentBookmarksController {

    /**
     * @see [RecentBookmarksInteractor.onRecentBookmarkClicked]
     */
    fun handleBookmarkClicked(bookmark: RecentBookmark)

    /**
     * @see [RecentBookmarksInteractor.onShowAllBookmarksClicked]
     */
    fun handleShowAllBookmarksClicked()

    /**
     * @see [RecentBookmarksInteractor.onRecentBookmarkRemoved]
     */
    fun handleBookmarkRemoved(bookmark: RecentBookmark)
}

/**
 * The default implementation of [RecentBookmarksController].
 */
class DefaultRecentBookmarksController(
    private val activity: HomeActivity,
    private val navController: NavController,
    private val appStore: AppStore,
) : RecentBookmarksController {

    override fun handleBookmarkClicked(bookmark: RecentBookmark) {
        activity.openToBrowserAndLoad(
            searchTermOrURL = bookmark.url!!,
            newTab = true,
            from = BrowserDirection.FromHome,
            flags = EngineSession.LoadUrlFlags.select(ALLOW_JAVASCRIPT_URL),
        )
        RecentBookmarks.bookmarkClicked.add()
    }

    override fun handleShowAllBookmarksClicked() {
        RecentBookmarks.showAllBookmarks.add()
        navController.navigate(
            HomeFragmentDirections.actionGlobalBookmarkFragment(BookmarkRoot.Mobile.id),
        )
    }

    override fun handleBookmarkRemoved(bookmark: RecentBookmark) {
        appStore.dispatch(AppAction.RemoveRecentBookmark(bookmark))
    }
}
