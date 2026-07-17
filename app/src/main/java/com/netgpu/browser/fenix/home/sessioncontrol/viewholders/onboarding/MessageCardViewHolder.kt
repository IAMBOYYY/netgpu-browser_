/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.home.sessioncontrol.viewholders.onboarding

import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import com.netgpu.browser.R
import com.netgpu.browser.components.components
import com.netgpu.browser.compose.ComposeViewHolder
import com.netgpu.browser.compose.MessageCard
import com.netgpu.browser.compose.MessageCardColors
import com.netgpu.browser.gleanplumb.Message
import com.netgpu.browser.home.sessioncontrol.SessionControlInteractor
import com.netgpu.browser.theme.NetGpuBrowserTheme
import com.netgpu.browser.wallpapers.Wallpaper
import com.netgpu.browser.wallpapers.WallpaperState

/**
 * View holder for the Nimbus Message Card.
 *
 * @property interactor [SessionControlInteractor] which will have delegated to all user
 * interactions.
 */
class MessageCardViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: SessionControlInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {
    private lateinit var messageGlobal: Message

    companion object {
        internal val LAYOUT_ID = View.generateViewId()
    }

    init {
        val horizontalPadding =
            composeView.resources.getDimensionPixelSize(R.dimen.home_item_horizontal_margin)
        composeView.setPadding(horizontalPadding, 0, horizontalPadding, 0)
    }

    fun bind(message: Message) {
        messageGlobal = message
    }

    @Composable
    override fun Content() {
        val message by remember { mutableStateOf(messageGlobal) }
        val wallpaperState = components.appStore
            .observeAsComposableState { state -> state.wallpaperState }.value ?: WallpaperState.default
        val isWallpaperNotDefault = !Wallpaper.nameIsDefault(wallpaperState.currentWallpaper.name)

        var (_, _, _, _, buttonColor, buttonTextColor) = MessageCardColors.buildMessageCardColors()

        if (isWallpaperNotDefault) {
            buttonColor = NetGpuBrowserTheme.colors.layer1

            if (!isSystemInDarkTheme()) {
                buttonTextColor = NetGpuBrowserTheme.colors.textActionSecondary
            }
        }

        val messageCardColors = MessageCardColors.buildMessageCardColors(
            backgroundColor = wallpaperState.wallpaperCardColor,
            buttonColor = buttonColor,
            buttonTextColor = buttonTextColor,
        )

        MessageCard(
            messageText = message.data.text,
            titleText = message.data.title,
            buttonText = message.data.buttonLabel,
            messageColors = messageCardColors,
            onClick = { interactor.onMessageClicked(message) },
            onCloseButtonClick = { interactor.onMessageClosedClicked(message) },
        )
    }
}
