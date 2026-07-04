/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import android.content.Context
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.runBlocking
import mozilla.components.browser.storage.sync.PlacesHistoryStorage
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.R
import com.netgpu.browser.customannotations.SmokeTest
import com.netgpu.browser.ext.settings
import com.netgpu.browser.helpers.AndroidAssetDispatcher
import com.netgpu.browser.helpers.HomeActivityTestRule
import com.netgpu.browser.helpers.RecyclerViewIdlingResource
import com.netgpu.browser.helpers.TestAssetHelper
import com.netgpu.browser.helpers.TestHelper.longTapSelectItem
import com.netgpu.browser.helpers.TestHelper.registerAndCleanupIdlingResources
import com.netgpu.browser.ui.robots.historyMenu
import com.netgpu.browser.ui.robots.homeScreen
import com.netgpu.browser.ui.robots.multipleSelectionToolbar
import com.netgpu.browser.ui.robots.navigationToolbar

/**
 *  Tests for verifying basic functionality of history
 *
 */
class HistoryTest {
    /* ktlint-disable no-blank-line-before-rbrace */ // This imposes unreadable grouping.
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mDevice: UiDevice

    @get:Rule
    val activityTestRule = HomeActivityTestRule.withDefaultSettingsOverrides()

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext.settings()
            .shouldShowJumpBackInCFR = false

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mockWebServer = MockWebServer().apply {
            dispatcher = AndroidAssetDispatcher()
            start()
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        // Clearing all history data after each test to avoid overlapping data
        val applicationContext: Context = activityTestRule.activity.applicationContext
        val historyStorage = PlacesHistoryStorage(applicationContext)

        runBlocking {
            historyStorage.deleteEverything()
        }
    }

    @Test
    fun noHistoryItemsInCacheTest() {
        homeScreen {
        }.openThreeDotMenu {
            verifyHistoryButton()
        }.openHistory {
            verifyHistoryMenuView()
            verifyEmptyHistoryView()
        }
    }

    // Test running on beta/release builds in CI:
    // caution when making changes to it, so they don't block the builds
    @Test
    fun visitedUrlHistoryTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                verifyHistoryMenuView()
                verifyVisitedTimeTitle()
                verifyFirstTestPageTitle("Test_Page_1")
                verifyTestPageUrl(firstWebPage.url)
            }
        }
    }

    @Test
    fun deleteHistoryItemTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                clickDeleteHistoryButton(firstWebPage.url.toString())
            }
            verifyDeleteSnackbarText("Deleted")
            verifyEmptyHistoryView()
        }
    }

    @Test
    fun undoDeleteHistoryItemTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                clickDeleteHistoryButton(firstWebPage.url.toString())
            }
            verifyUndoDeleteSnackBarButton()
            clickUndoDeleteButton()
            verifyHistoryItemExists(true, firstWebPage.url.toString())
        }
    }

    @SmokeTest
    @Test
    fun cancelDeleteAllHistoryTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                clickDeleteAllHistoryButton()

            }
            verifyDeleteConfirmationMessage()
            selectEverythingOption()
            cancelDeleteHistory()
            verifyHistoryItemExists(true, firstWebPage.url.toString())
        }
    }

    @SmokeTest
    @Test
    fun deleteAllHistoryTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                clickDeleteAllHistoryButton()
            }
            verifyDeleteConfirmationMessage()
            selectEverythingOption()
            confirmDeleteAllHistory()
            verifyDeleteSnackbarText("Browsing data deleted")
            verifyEmptyHistoryView()
        }
    }

    @SmokeTest
    @Test
    fun historyMultiSelectionToolbarItemsTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                longTapSelectItem(firstWebPage.url)
            }
        }

        multipleSelectionToolbar {
            verifyMultiSelectionCheckmark()
            verifyMultiSelectionCounter()
            verifyShareHistoryButton()
            verifyCloseToolbarButton()
        }.closeToolbarReturnToHistory {
            verifyHistoryMenuView()
        }
    }

    @Test
    fun openHistoryInNewTabTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openTabDrawer {
            closeTab()
        }

        homeScreen { }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                longTapSelectItem(firstWebPage.url)
                openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
            }
        }

        multipleSelectionToolbar {
        }.clickOpenNewTab {
            verifyExistingTabList()
            verifyNormalModeSelected()
        }
    }

    @Test
    fun openHistoryInPrivateTabTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                longTapSelectItem(firstWebPage.url)
                openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
            }
        }

        multipleSelectionToolbar {
        }.clickOpenPrivateTab {
            verifyPrivateModeSelected()
            verifyExistingTabList()
        }
    }

    @Test
    fun deleteMultipleSelectionTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)
        val secondWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 2)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
        }.openNavigationToolbar {
        }.enterURLAndEnterToBrowser(secondWebPage.url) {
            mDevice.waitForIdle()
            verifyUrl(secondWebPage.url.toString())
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 2),
            ) {
                verifyHistoryItemExists(true, firstWebPage.url.toString())
                verifyHistoryItemExists(true, secondWebPage.url.toString())
                longTapSelectItem(firstWebPage.url)
                longTapSelectItem(secondWebPage.url)
                openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
            }
        }

        multipleSelectionToolbar {
            clickMultiSelectionDelete()
        }

        historyMenu {
            verifyEmptyHistoryView()
        }
    }

    @Test
    fun shareButtonTest() {
        val firstWebPage = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
            mDevice.waitForIdle()
        }.openThreeDotMenu {
        }.openHistory {
            verifyHistoryListExists()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.history_list), 1),
            ) {
                longTapSelectItem(firstWebPage.url)
            }
        }

        multipleSelectionToolbar {
            clickShareHistoryButton()
            verifyShareOverlay()
            verifyShareTabFavicon()
            verifyShareTabTitle()
            verifyShareTabUrl()
        }
    }

    // This test verifies the Recently Closed Tabs List and items
    @Test
    fun verifyRecentlyClosedTabsListTest() {
        val website = TestAssetHelper.getGenericAsset(mockWebServer, 1)

        homeScreen {
        }.openNavigationToolbar {
        }.enterURLAndEnterToBrowser(website.url) {
            mDevice.waitForIdle()
        }.openTabDrawer {
            closeTab()
        }.openTabDrawer {
        }.openRecentlyClosedTabs {
            waitForListToExist()
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.recently_closed_list), 1),
            ) {
                verifyRecentlyClosedTabsMenuView()
            }
            verifyRecentlyClosedTabsPageTitle("Test_Page_1")
            verifyRecentlyClosedTabsUrl(website.url)
        }
    }
}
