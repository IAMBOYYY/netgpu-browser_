/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import android.view.View
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.R
import com.netgpu.browser.customannotations.SmokeTest
import com.netgpu.browser.ext.settings
import com.netgpu.browser.helpers.AndroidAssetDispatcher
import com.netgpu.browser.helpers.HomeActivityIntentTestRule
import com.netgpu.browser.helpers.RecyclerViewIdlingResource
import com.netgpu.browser.helpers.TestAssetHelper.getEnhancedTrackingProtectionAsset
import com.netgpu.browser.helpers.TestAssetHelper.getGenericAsset
import com.netgpu.browser.helpers.TestHelper.registerAndCleanupIdlingResources
import com.netgpu.browser.helpers.ViewVisibilityIdlingResource
import com.netgpu.browser.ui.robots.addonsMenu
import com.netgpu.browser.ui.robots.homeScreen
import com.netgpu.browser.ui.robots.navigationToolbar

/**
 *  Tests for verifying the functionality of installing or removing addons
 *
 */
class SettingsAddonsTest {
    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    val activityTestRule = HomeActivityIntentTestRule.withDefaultSettingsOverrides()

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

    // Walks through settings add-ons menu to ensure all items are present
    @Test
    fun settingsAddonsItemsTest() {
        homeScreen {
        }.openThreeDotMenu {
        }.openSettings {
            verifyAdvancedHeading()
            verifyAddons()
        }.openAddonsManagerMenu {
            registerAndCleanupIdlingResources(
                RecyclerViewIdlingResource(activityTestRule.activity.findViewById(R.id.add_ons_list), 1),
            ) {
                verifyAddonsItems()
            }
        }
    }

    // Installs an add-on from the Add-ons menu and verifies the prompts
    @Test
    fun installAddonTest() {
        val addonName = "uBlock Origin"

        homeScreen {}
            .openThreeDotMenu {}
            .openAddonsManagerMenu {
                registerAndCleanupIdlingResources(
                    RecyclerViewIdlingResource(
                        activityTestRule.activity.findViewById(R.id.add_ons_list),
                        1,
                    ),
                ) {
                    clickInstallAddon(addonName)
                }
                verifyAddonPermissionPrompt(addonName)
                cancelInstallAddon()
                clickInstallAddon(addonName)
                acceptPermissionToInstallAddon()
                verifyAddonInstallCompleted(addonName, activityTestRule)
                verifyAddonInstallCompletedPrompt(addonName)
                closeAddonInstallCompletePrompt()
                verifyAddonIsInstalled(addonName)
                verifyEnabledTitleDisplayed()
            }
    }

    // Installs an addon, then uninstalls it
    @Test
    fun verifyAddonsCanBeUninstalled() {
        val addonName = "uBlock Origin"

        addonsMenu {
            installAddon(addonName)
            verifyAddonInstallCompleted(addonName, activityTestRule)
            closeAddonInstallCompletePrompt()
        }.openDetailedMenuForAddon(addonName) {
            registerAndCleanupIdlingResources(
                ViewVisibilityIdlingResource(
                    activityTestRule.activity.findViewById(R.id.addon_container),
                    View.VISIBLE,
                ),
            ) {}
        }.removeAddon {
            verifyAddonCanBeInstalled(addonName)
        }
    }

    // Installs uBlock add-on and checks that the app doesn't crash while loading pages with trackers
    @SmokeTest
    @Test
    fun noCrashWithAddonInstalledTest() {
        // setting ETP to Strict mode to test it works with add-ons
        activityTestRule.activity.settings().setStrictETP()

        val addonName = "uBlock Origin"
        val trackingProtectionPage = getEnhancedTrackingProtectionAsset(mockWebServer)

        addonsMenu {
            installAddon(addonName)
            verifyAddonInstallCompleted(addonName, activityTestRule)
            closeAddonInstallCompletePrompt()
        }.goBack {
        }.openNavigationToolbar {
        }.enterURLAndEnterToBrowser(trackingProtectionPage.url) {
            verifyUrl(trackingProtectionPage.url.toString())
        }
    }

    @SmokeTest
    @Test
    fun useAddonsInPrivateModeTest() {
        val addonName = "uBlock Origin"
        val genericPage = getGenericAsset(mockWebServer, 1)

        addonsMenu {
            installAddon(addonName)
            verifyAddonInstallCompleted(addonName, activityTestRule)
            selectAllowInPrivateBrowsing()
            closeAddonInstallCompletePrompt()
        }.goBack {
        }.togglePrivateBrowsingMode()
        navigationToolbar {
        }.enterURLAndEnterToBrowser(genericPage.url) {
            verifyPageContent(genericPage.content)
        }.openThreeDotMenu {
            openAddonsSubList()
            verifyAddonAvailableInMainMenu(addonName)
        }
    }
}
