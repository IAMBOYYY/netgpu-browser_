/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import mozilla.telemetry.glean.private.NoExtras
import com.netgpu.browser.R
import com.netgpu.browser.compose.button.PrimaryButton
import com.netgpu.browser.compose.button.SecondaryButton
import com.netgpu.browser.theme.NetGpuBrowserTheme
import com.netgpu.browser.GleanMetrics.Onboarding as OnboardingMetrics

/**
 * Enum that represents the onboarding screen that is displayed.
 */
private enum class OnboardingState {
    Welcome,
    SyncSignIn,
}

/**
 * A screen for displaying a welcome and sync sign in onboarding.
 *
 * @param isSyncSignIn Whether or not the user is signed into their NETGPU BROWSER Sync account.
 * @param onDismiss Invoked when the user clicks on the close or "Skip" button.
 * @param onSignInButtonClick Invoked when the user clicks on the "Sign In" button
 */
@Composable
fun Onboarding(
    isSyncSignIn: Boolean,
    onDismiss: () -> Unit,
    onSignInButtonClick: () -> Unit,
) {
    var onboardingState by remember { mutableStateOf(OnboardingState.Welcome) }

    Column(
        modifier = Modifier
            .background(NetGpuBrowserTheme.colors.layer1)
            .fillMaxSize()
            .padding(bottom = 32.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = {
                    if (onboardingState == OnboardingState.Welcome) {
                        OnboardingMetrics.welcomeCloseClicked.record(NoExtras())
                    } else {
                        OnboardingMetrics.syncCloseClicked.record(NoExtras())
                    }
                    onDismiss()
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mozac_ic_close),
                    contentDescription = stringResource(R.string.onboarding_home_content_description_close_button),
                    tint = NetGpuBrowserTheme.colors.iconPrimary,
                )
            }
        }

        if (onboardingState == OnboardingState.Welcome) {
            OnboardingWelcomeContent()

            OnboardingWelcomeBottomContent(
                onboardingState = onboardingState,
                isSyncSignIn = isSyncSignIn,
                onGetStartedButtonClick = {
                    OnboardingMetrics.welcomeGetStartedClicked.record(NoExtras())
                    if (isSyncSignIn) {
                        onDismiss()
                    } else {
                        onboardingState = OnboardingState.SyncSignIn
                    }
                },
            )

            OnboardingMetrics.welcomeCardImpression.record(NoExtras())
        } else if (onboardingState == OnboardingState.SyncSignIn) {
            OnboardingSyncSignInContent()

            OnboardingSyncSignInBottomContent(
                onboardingState = onboardingState,
                onSignInButtonClick = {
                    OnboardingMetrics.syncSignInClicked.record(NoExtras())
                    onSignInButtonClick()
                },
                onSkipButtonClick = {
                    OnboardingMetrics.syncSkipClicked.record(NoExtras())
                    onDismiss()
                },
            )

            OnboardingMetrics.syncCardImpression.record(NoExtras())
        }
    }
}

@Composable
private fun OnboardingWelcomeBottomContent(
    onboardingState: OnboardingState,
    isSyncSignIn: Boolean,
    onGetStartedButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        PrimaryButton(
            text = stringResource(id = R.string.onboarding_home_get_started_button),
            onClick = onGetStartedButtonClick,
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isSyncSignIn) {
            Spacer(modifier = Modifier.height(6.dp))
        } else {
            Indicators(onboardingState = onboardingState)
        }
    }
}

@Composable
private fun OnboardingWelcomeContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_welcome),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.onboarding_home_welcome_title_2),
            color = NetGpuBrowserTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = NetGpuBrowserTheme.typography.headline5,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.onboarding_home_welcome_description),
            color = NetGpuBrowserTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            style = NetGpuBrowserTheme.typography.body2,
        )
    }
}

@Composable
private fun OnboardingSyncSignInContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_sync),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.onboarding_home_sync_title_3),
            color = NetGpuBrowserTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = NetGpuBrowserTheme.typography.headline5,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.onboarding_home_sync_description),
            color = NetGpuBrowserTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            style = NetGpuBrowserTheme.typography.body2,
        )
    }
}

@Composable
private fun OnboardingSyncSignInBottomContent(
    onboardingState: OnboardingState,
    onSignInButtonClick: () -> Unit,
    onSkipButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        PrimaryButton(
            text = stringResource(id = R.string.onboarding_home_sign_in_button),
            onClick = onSignInButtonClick,
        )

        Spacer(modifier = Modifier.height(8.dp))

        SecondaryButton(
            text = stringResource(id = R.string.onboarding_home_skip_button),
            onClick = onSkipButtonClick,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Indicators(onboardingState = onboardingState)
    }
}

@Composable
private fun Indicators(onboardingState: OnboardingState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Indicator(
            color = if (onboardingState == OnboardingState.Welcome) {
                NetGpuBrowserTheme.colors.indicatorActive
            } else {
                NetGpuBrowserTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(8.dp))

        Indicator(
            color = if (onboardingState == OnboardingState.SyncSignIn) {
                NetGpuBrowserTheme.colors.indicatorActive
            } else {
                NetGpuBrowserTheme.colors.indicatorInactive
            },
        )
    }
}

@Composable
private fun Indicator(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
@Preview
private fun OnboardingPreview() {
    NetGpuBrowserTheme {
        Onboarding(
            isSyncSignIn = false,
            onDismiss = {},
            onSignInButtonClick = {},
        )
    }
}
