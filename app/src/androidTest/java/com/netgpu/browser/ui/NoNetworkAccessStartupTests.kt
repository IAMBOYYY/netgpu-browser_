/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import androidx.core.net.toUri
import org.junit.After
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.R
import com.netgpu.browser.helpers.HomeActivityTestRule
import com.netgpu.browser.helpers.TestHelper.packageName
import com.netgpu.browser.helpers.TestHelper.setNetworkEnabled
import com.netgpu.browser.helpers.TestHelper.verifyUrl
import com.netgpu.browser.ui.robots.browserScreen
import com.netgpu.browser.ui.robots.homeScreen
import com.netgpu.browser.ui.robots.navigationToolbar

/**
 * Tests to verify some main UI flows with Network connection off
 *
 */

class NoNetworkAccessStartupTests {

    @get:Rule
    val activityTestRule = HomeActivityTestRule.withDefaultSettingsOverrides(launchActivity = false)

    @After
    fun tearDown() {
        // Restoring network connection
        setNetworkEnabled(true)
    }

    // Test running on beta/release builds in CI:
    // caution when making changes to it, so they don't block the builds
    // Based on STR from https://github.com/mozilla-mobile/netgpu_browser/issues/16886
    @Test
    fun noNetworkConnectionStartupTest() {
        setNetworkEnabled(false)

        activityTestRule.launchActivity(null)

        homeScreen {
        }.dismissOnboarding()
        homeScreen {
            verifyHomeScreen()
        }
    }

    // Based on STR from https://github.com/mozilla-mobile/netgpu_browser/issues/16886
    @Test
    fun networkInterruptedFromBrowserToHomeTest() {
        val url = "example.com"

        activityTestRule.launchActivity(null)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(url.toUri()) {}

        setNetworkEnabled(false)

        browserScreen {
        }.goToHomescreen {
            verifyHomeScreen()
        }
    }

    @Test
    fun testPageReloadAfterNetworkInterrupted() {
        val url = "example.com"

        activityTestRule.launchActivity(null)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(url.toUri()) {}

        setNetworkEnabled(false)

        browserScreen {
        }.openThreeDotMenu {
        }.refreshPage { }
    }

    @Test
    fun testSignInPageWithNoNetworkConnection() {
        setNetworkEnabled(false)

        activityTestRule.launchActivity(null)

        homeScreen {
        }.openThreeDotMenu {
        }.openSettings {
        }.openTurnOnSyncMenu {
            tapOnUseEmailToSignIn()
            verifyUrl(
                "firefox.com",
                "$packageName:id/mozac_browser_toolbar_url_view",
                R.id.mozac_browser_toolbar_url_view,
            )
        }
    }
}
