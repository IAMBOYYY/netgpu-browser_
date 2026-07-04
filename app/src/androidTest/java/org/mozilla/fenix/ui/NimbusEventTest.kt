/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.mockk.mockk
import mozilla.components.concept.sync.AuthType
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.components.TelemetryAccountObserver
import com.netgpu.browser.helpers.AndroidAssetDispatcher
import com.netgpu.browser.helpers.Experimentation
import com.netgpu.browser.helpers.HomeActivityIntentTestRule
import com.netgpu.browser.helpers.RetryTestRule
import com.netgpu.browser.helpers.TestHelper.appContext
import com.netgpu.browser.ui.robots.homeScreen

class NimbusEventTest {
    private lateinit var mDevice: UiDevice
    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    val homeActivityTestRule = HomeActivityIntentTestRule.withDefaultSettingsOverrides()
        .withIntent(
            Intent().apply {
                action = Intent.ACTION_VIEW
            },
        )

    @Rule
    @JvmField
    val retryTestRule = RetryTestRule(3)

    @Before
    fun setUp() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
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
    fun homeScreenNimbusEventsTest() {
        homeScreen { }.dismissOnboarding()

        Experimentation.withHelper {
            assertTrue(evalJexl("'app_opened'|eventSum('Days', 28, 0) > 0"))
        }
    }

    @Test
    fun telemetryAccountObserverTest() {
        val observer = TelemetryAccountObserver(appContext)
        observer.onAuthenticated(mockk(), AuthType.Signin)

        Experimentation.withHelper {
            assertTrue(evalJexl("'sync_auth.sign_in'|eventSum('Days', 28, 0) > 0"))
        }
    }
}
