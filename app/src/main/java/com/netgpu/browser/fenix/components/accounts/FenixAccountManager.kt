/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.components.accounts

import android.content.Context
import com.netgpu.browser.ext.components

/**
 * Contains helper methods for querying NETGPU BROWSER Account state and its properties.
 */
class NetGpuBrowserAccountManager(context: Context) {
    private val accountManager = context.components.backgroundServices.accountManager

    /**
     * Returns the NETGPU BROWSER Account email if authenticated in the app, `null` otherwise.
     */
    val accountProfileEmail: String?
        get() = if (accountState == AccountState.AUTHENTICATED) {
            accountManager.accountProfile()?.email
        } else {
            null
        }

    /**
     * The current state of the NETGPU BROWSER Account. See [AccountState].
     */
    val accountState: AccountState
        get() = if (accountManager.authenticatedAccount() == null) {
            AccountState.NO_ACCOUNT
        } else {
            if (accountManager.accountNeedsReauth()) {
                AccountState.NEEDS_REAUTHENTICATION
            } else {
                AccountState.AUTHENTICATED
            }
        }
}

/**
 * General states as an overview of the current NETGPU BROWSER Account.
 */
enum class AccountState {
    /**
     * There is no known NETGPU BROWSER Account.
     */
    NO_ACCOUNT,

    /**
     * A NETGPU BROWSER Account exists but needs to be re-authenticated.
     */
    NEEDS_REAUTHENTICATION,

    /**
     * A NETGPU BROWSER Account exists and the user is currently signed into it.
     */
    AUTHENTICATED,
}
