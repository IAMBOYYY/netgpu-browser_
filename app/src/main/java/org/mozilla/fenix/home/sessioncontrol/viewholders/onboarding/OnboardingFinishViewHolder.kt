/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.sessioncontrol.viewholders.onboarding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.service.glean.private.NoExtras
import com.netgpu.browser.GleanMetrics.Onboarding
import com.netgpu.browser.R
import com.netgpu.browser.databinding.OnboardingFinishBinding
import com.netgpu.browser.home.sessioncontrol.OnboardingInteractor

class OnboardingFinishViewHolder(
    view: View,
    private val interactor: OnboardingInteractor,
) : RecyclerView.ViewHolder(view) {

    init {
        val binding = OnboardingFinishBinding.bind(view)
        binding.finishButton.setOnClickListener {
            interactor.onStartBrowsingClicked()
            Onboarding.finish.record(NoExtras())
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.onboarding_finish
    }
}
