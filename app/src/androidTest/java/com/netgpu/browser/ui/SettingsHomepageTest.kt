/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.customannotations.SmokeTest
import com.netgpu.browser.helpers.AndroidAssetDispatcher
import com.netgpu.browser.helpers.HomeActivityIntentTestRule
import com.netgpu.browser.helpers.RetryTestRule
import com.netgpu.browser.helpers.TestAssetHelper.getGenericAsset
import com.netgpu.browser.helpers.TestHelper.mDevice
import com.netgpu.browser.helpers.TestHelper.openAppFromExternalLink
import com.netgpu.browser.helpers.TestHelper.restartApp
import com.netgpu.browser.ui.robots.browserScreen
import com.netgpu.browser.ui.robots.homeScreen
import com.netgpu.browser.ui.robots.navigationToolbar

/**
 *  Tests for verifying the Homepage settings menu
 *
 */
class SettingsHomepageTest {
    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    val activityIntentTestRule = HomeActivityIntentTestRule.withDefaultSettingsOverrides(skipOnboarding = true)

    @Rule
    @JvmField
    val retryTestRule = RetryTestRule(3)

    @Before
    fun setUp() {
        mockWebServer = MockWebServer().apply {
            dispatcher = AndroidAssetDispatcher()
            start()
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun verifyHomepageSettingsTest() {
        homeScreen {
        }.openThreeDotMenu {
        }.openSettings {
        }.openHomepageSubMenu {
            verifyHomePageView()
        }
    }

    @Test
    fun verifyShortcutOptionTest() {
        // en-US defaults
        val defaultTopSites = arrayOf(
            "Top Articles",
            "Wikipedia",
            "Google",
        )

        homeScreen {
            defaultTopSites.forEach { item ->
                verifyExistingTopSitesTabs(item)
            }
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickShortcutsButton()
        }.goBack {
            defaultTopSites.forEach { item ->
                verifyNotExistingTopSitesList(item)
            }
        }
    }

    @Test
    fun verifyRecentlyVisitedOptionTest() {
        activityIntentTestRule.applySettingsExceptions {
            it.isRecentTabsFeatureEnabled = false
        }
        val genericURL = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericURL.url) {
        }.goToHomescreen {
            verifyRecentlyVisitedSectionIsDisplayed()
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickRecentlyVisited()
        }.goBack {
            verifyRecentlyVisitedSectionIsNotDisplayed()
        }
    }

    @Test
    fun verifyPocketOptionTest() {
        activityIntentTestRule.applySettingsExceptions {
            it.isRecentTabsFeatureEnabled = false
            it.isRecentlyVisitedFeatureEnabled = false
        }
        val genericURL = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericURL.url) {
        }.goToHomescreen {
            verifyPocketSectionIsDisplayed()
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickPocketButton()
        }.goBack {
            verifyPocketSectionIsNotDisplayed()
        }
    }

    @SmokeTest
    @Test
    fun jumpBackInOptionTest() {
        val genericURL = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericURL.url) {
        }.goToHomescreen {
            verifyJumpBackInSectionIsDisplayed()
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickJumpBackInButton()
        }.goBack {
            verifyJumpBackInSectionIsNotDisplayed()
        }
    }

    @SmokeTest
    @Test
    fun recentBookmarksOptionTest() {
        val genericURL = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericURL.url) {
        }.openThreeDotMenu {
        }.bookmarkPage {
        }.goToHomescreen {
            verifyRecentBookmarksSectionIsDisplayed()
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickRecentBookmarksButton()
        }.goBack {
            verifyRecentBookmarksSectionIsNotDisplayed()
        }
    }

    @SmokeTest
    @Test
    fun startOnHomepageTest() {
        val genericURL = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericURL.url) {
        }.openThreeDotMenu {
        }.openSettings {
        }.openHomepageSubMenu {
            clickStartOnHomepageButton()
        }

        restartApp(activityIntentTestRule)

        homeScreen {
            verifyHomeScreen()
        }
    }

    @SmokeTest
    @Test
    fun startOnLastTabTest() {
        val firstWebPage = getGenericAsset(mockWebServer, 1)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(firstWebPage.url) {
        }.goToHomescreen {
        }.openThreeDotMenu {
        }.openCustomizeHome {
            clickStartOnLastTabButton()
        }

        restartApp(activityIntentTestRule)

        browserScreen {
            verifyUrl(firstWebPage.url.toString())
        }
    }

    @Test
    fun ignoreStartOnHomeWhenLaunchedByExternalLinkTest() {
        val genericPage = getGenericAsset(mockWebServer, 1)

        homeScreen {
        }.openThreeDotMenu {
        }.openSettings {
        }.openHomepageSubMenu {
            clickStartOnHomepageButton()
        }.goBack {}

        with(activityIntentTestRule) {
            finishActivity()
            mDevice.waitForIdle()
            this.applySettingsExceptions {
                it.isTCPCFREnabled = false
            }
            openAppFromExternalLink(genericPage.url.toString())
        }

        browserScreen {
            verifyPageContent(genericPage.content)
        }
    }

    @SmokeTest
    @Test
    @Ignore("Intermittent test: https://github.com/mozilla-mobile/netgpu_browser/issues/26559")
    fun setWallpaperTest() {
        val wallpapers = listOf(
            "Wallpaper Item: amethyst",
            "Wallpaper Item: cerulean",
            "Wallpaper Item: sunrise",
        )

        for (wallpaper in wallpapers) {
            homeScreen {
            }.openThreeDotMenu {
            }.openCustomizeHome {
                openWallpapersMenu()
                selectWallpaper(wallpaper)
                verifySnackBarText("Wallpaper updated!")
            }.clickSnackBarViewButton {
                verifyWallpaperImageApplied(true)
            }
        }
    }
}
