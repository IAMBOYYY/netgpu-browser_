/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.customannotations.SmokeTest
import com.netgpu.browser.helpers.HomeActivityTestRule
import com.netgpu.browser.helpers.TestHelper.mDevice
import com.netgpu.browser.ui.robots.homeScreen

/**
 *  Tests for verifying the new onboarding features.
 *  Note: This involves setting the feature flag On for the onboarding dialog
 *
 */
class OnboardingFeaturesTest {

    @get:Rule
    val activityTestRule = AndroidComposeTestRule(
        HomeActivityTestRule(isHomeOnboardingDialogEnabled = true),
    ) { it.activity }

    @SmokeTest
    @Test
    fun upgradingUsersOnboardingScreensTest() {
        homeScreen {
            verifyUpgradingUserOnboardingFirstScreen(activityTestRule)
            clickGetStartedButton(activityTestRule)
            verifyUpgradingUserOnboardingSecondScreen(activityTestRule)
            clickSkipButton(activityTestRule)
            verifyHomeScreen()
        }
    }

    @Test
    fun upgradingUsersOnboardingSignInButtonTest() {
        homeScreen {
            verifyUpgradingUserOnboardingFirstScreen(activityTestRule)
            clickGetStartedButton(activityTestRule)
            verifyUpgradingUserOnboardingSecondScreen(activityTestRule)
        }.clickUpgradingUserOnboardingSignInButton(activityTestRule) {
            verifyTurnOnSyncMenu()
            mDevice.pressBack()
        }
        homeScreen {
            verifyHomeScreen()
        }
    }
}
