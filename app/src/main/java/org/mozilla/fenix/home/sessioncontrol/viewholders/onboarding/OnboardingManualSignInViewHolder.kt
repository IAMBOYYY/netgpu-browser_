/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.sessioncontrol.viewholders.onboarding

import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.service.glean.private.NoExtras
import com.netgpu.browser.GleanMetrics.Onboarding
import com.netgpu.browser.R
import com.netgpu.browser.databinding.OnboardingManualSigninBinding
import com.netgpu.browser.home.HomeFragmentDirections

class OnboardingManualSignInViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var binding: OnboardingManualSigninBinding = OnboardingManualSigninBinding.bind(view)

    init {
        binding.fxaSignInButton.setOnClickListener {
            Onboarding.fxaManualSignin.record(NoExtras())

            val directions = HomeFragmentDirections.actionGlobalTurnOnSync()
            Navigation.findNavController(view).navigate(directions)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.onboarding_manual_signin
    }
}
