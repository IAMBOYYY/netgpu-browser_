/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.ui.robots

import com.netgpu.browser.R
import com.netgpu.browser.helpers.MatcherHelper.assertCheckedItemWithResIdExists
import com.netgpu.browser.helpers.MatcherHelper.assertItemContainingTextExists
import com.netgpu.browser.helpers.MatcherHelper.checkedItemWithResId
import com.netgpu.browser.helpers.MatcherHelper.itemContainingText
import com.netgpu.browser.helpers.MatcherHelper.itemWithResId
import com.netgpu.browser.helpers.TestHelper.getStringResource
import com.netgpu.browser.helpers.TestHelper.packageName

/**
 * Implementation of Robot Pattern for the settings Cookie Banner Reduction sub menu.
 */
class SettingsSubMenuCookieBannerReductionRobot {
    fun verifyCookieBannerView(isCookieBannerReductionChecked: Boolean) {
        assertItemContainingTextExists(cookieBannerOptionTitle, cookieBannerOptionDescription)
        assertCheckedItemWithResIdExists(checkedCookieBannerOptionToggle(isCookieBannerReductionChecked))
    }
    fun clickCookieBannerReductionToggle() = cookieBannerOptionToggle.click()
    fun verifyCheckedCookieBannerReductionToggle(isCookieBannerReductionChecked: Boolean) =
        assertCheckedItemWithResIdExists(checkedCookieBannerOptionToggle(isCookieBannerReductionChecked))

    class Transition
}

private val cookieBannerOptionTitle =
    itemContainingText(getStringResource(R.string.reduce_cookie_banner_option))
private val cookieBannerOptionDescription =
    itemContainingText(getStringResource(R.string.reduce_cookie_banner_summary_1))
private val cookieBannerOptionToggle =
    itemWithResId("$packageName:id/learn_more_switch")
private fun checkedCookieBannerOptionToggle(isChecked: Boolean = false) =
    checkedItemWithResId("$packageName:id/learn_more_switch", isChecked)
