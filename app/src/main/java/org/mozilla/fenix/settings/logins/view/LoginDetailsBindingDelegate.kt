/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.settings.logins.view

import com.netgpu.browser.databinding.FragmentLoginDetailBinding
import com.netgpu.browser.settings.logins.LoginsListState

/**
 * View that contains and configures the Login Details
 */
class LoginDetailsBindingDelegate(
    private val binding: FragmentLoginDetailBinding,
) {
    fun update(login: LoginsListState) {
        binding.webAddressText.text = login.currentItem?.origin
        binding.usernameText.text = login.currentItem?.username
        binding.passwordText.text = login.currentItem?.password
    }
}
