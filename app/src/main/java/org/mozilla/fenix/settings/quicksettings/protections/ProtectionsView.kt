/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.settings.quicksettings.protections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import com.netgpu.browser.R
import com.netgpu.browser.compose.annotation.LightDarkPreview
import com.netgpu.browser.databinding.QuicksettingsProtectionsPanelBinding
import com.netgpu.browser.theme.NetGpuBrowserTheme
import com.netgpu.browser.trackingprotection.ProtectionsState
import com.netgpu.browser.utils.Settings

/**
 * MVI View that displays the tracking protection, cookie banner handling toggles and the navigation
 * to additional tracking protection details.
 *
 * @param containerView [ViewGroup] in which this View will inflate itself.
 * @param interactor [ProtectionsInteractor] which will have delegated to all user
 * @param settings [Settings] application settings.
 * interactions.
 */
class ProtectionsView(
    val containerView: ViewGroup,
    val interactor: ProtectionsInteractor,
    val settings: Settings,
) {

    /**
     * Allows changing what this View displays.
     */
    fun update(state: ProtectionsState) {
        bindTrackingProtectionInfo(state.isTrackingProtectionEnabled)
        bindCookieBannerProtection(state.isCookieBannerHandlingEnabled)
        binding.trackingProtectionSwitch.isVisible = settings.shouldUseTrackingProtection
        binding.cookieBannerItem.isVisible = shouldShowCookieBanner

        binding.trackingProtectionDetails.setOnClickListener {
            interactor.onTrackingProtectionDetailsClicked()
        }
    }

    @VisibleForTesting
    internal fun updateDetailsSection(show: Boolean) {
        binding.trackingProtectionDetails.isVisible = show
    }

    private fun bindTrackingProtectionInfo(isTrackingProtectionEnabled: Boolean) {
        binding.trackingProtectionSwitch.isChecked = isTrackingProtectionEnabled
        binding.trackingProtectionSwitch.setOnCheckedChangeListener { _, isChecked ->
            interactor.onTrackingProtectionToggled(isChecked)
        }
    }

    @VisibleForTesting
    internal val binding = QuicksettingsProtectionsPanelBinding.inflate(
        LayoutInflater.from(containerView.context),
        containerView,
        true,
    )

    private val shouldShowCookieBanner: Boolean
        get() = settings.shouldShowCookieBannerUI && settings.shouldUseCookieBanner

    private fun bindCookieBannerProtection(isCookieBannerHandlingEnabled: Boolean) {
        val context = binding.cookieBannerItem.context
        val label = context.getString(R.string.preferences_cookie_banner_reduction)
        val description = context.getString(
            if (isCookieBannerHandlingEnabled) {
                R.string.reduce_cookie_banner_on_for_site
            } else {
                R.string.reduce_cookie_banner_off_for_site
            },
        )
        val icon = if (isCookieBannerHandlingEnabled) {
            R.drawable.ic_cookies_enabled
        } else {
            R.drawable.ic_cookies_disabled
        }

        binding.cookieBannerItem.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NetGpuBrowserTheme {
                    CookieBannerItem(
                        label = label,
                        description = description,
                        startIconPainter = painterResource(icon),
                        endIconPainter = painterResource(R.drawable.ic_arrowhead_right),
                        onClick = { interactor.onCookieBannerHandlingDetailsClicked() },
                    )
                }
            }
        }
    }
}

@Composable
private fun CookieBannerItem(
    label: String,
    description: String,
    startIconPainter: Painter,
    endIconPainter: Painter,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = startIconPainter,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 0.dp),
            tint = NetGpuBrowserTheme.colors.iconPrimary,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .weight(1f),
        ) {
            Text(
                text = label,
                color = NetGpuBrowserTheme.colors.textPrimary,
                style = NetGpuBrowserTheme.typography.subtitle1,
                maxLines = 1,
            )
            Text(
                text = description,
                color = NetGpuBrowserTheme.colors.textSecondary,
                style = NetGpuBrowserTheme.typography.body2,
                maxLines = 1,
            )
        }
        Icon(
            modifier = Modifier
                .padding(end = 0.dp)
                .size(24.dp),
            painter = endIconPainter,
            contentDescription = null,
            tint = NetGpuBrowserTheme.colors.iconPrimary,
        )
    }
}

@Composable
@LightDarkPreview
private fun CookieBannerItemPreview() {
    NetGpuBrowserTheme {
        Box(Modifier.background(NetGpuBrowserTheme.colors.layer1)) {
            CookieBannerItem(
                label = "Cookie Banner Reduction",
                description = "On for this site",
                startIconPainter = painterResource(R.drawable.ic_cookies_enabled),
                endIconPainter = painterResource(R.drawable.ic_arrowhead_right),
                onClick = { println("list item click") },
            )
        }
    }
}
