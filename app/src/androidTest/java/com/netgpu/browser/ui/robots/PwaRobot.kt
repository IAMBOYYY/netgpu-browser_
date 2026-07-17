/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui.robots

import androidx.test.uiautomator.UiSelector
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import com.netgpu.browser.helpers.TestHelper.isExternalAppBrowserActivityInCurrentTask
import com.netgpu.browser.helpers.TestHelper.mDevice
import com.netgpu.browser.helpers.TestHelper.packageName

class PwaRobot {

    fun verifyCustomTabToolbarIsNotDisplayed() = assertFalse(customTabToolbar().exists())
    fun verifyPwaActivityInCurrentTask() = assertTrue(isExternalAppBrowserActivityInCurrentTask())

    class Transition
}

fun pwaScreen(interact: PwaRobot.() -> Unit): PwaRobot.Transition {
    mDevice.findObject(UiSelector().resourceId("$packageName:id/engineView"))
    PwaRobot().interact()
    return PwaRobot.Transition()
}

private fun customTabToolbar() = mDevice.findObject(UiSelector().resourceId("$packageName:id/toolbar"))
