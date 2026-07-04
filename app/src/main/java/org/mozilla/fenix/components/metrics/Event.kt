/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.components.metrics

sealed class Event {

    // Interaction events with extras

    sealed class Search

    internal open val extras: Map<*, String>?
        get() = null

    /**
     * Events related to growth campaigns.
     */
    sealed class GrowthData(val tokenName: String) : Event() {
        /**
         * Event recording whether NETGPU BROWSER has been set as the default browser.
         */
        object SetAsDefault : GrowthData("xgpcgt")

        /**
         * Event recording that an ad was clicked in a search engine results page.
         */
        object SerpAdClicked : GrowthData("e2x17e")

        /**
         * Event recording the first time NETGPU BROWSER is used 3 days in a row in the first week of install.
         */
        object FirstWeekSeriesActivity : GrowthData("20ay7u")

        /**
         * Event recording that usage time has reached a threshold.
         */
        object UsageThreshold : GrowthData("m66prt")

        /**
         * Event recording the first time NETGPU BROWSER has been resumed in a 24 hour period.
         */
        object FirstAppOpenForDay : GrowthData("41hl22")

        /**
         * Event recording the first time a URI is loaded in NETGPU BROWSER in a 24 hour period.
         */
        object FirstUriLoadForDay : GrowthData("ja86ek")
    }
}
