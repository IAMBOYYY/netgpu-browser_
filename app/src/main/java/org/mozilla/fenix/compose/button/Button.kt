/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.compose.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.netgpu.browser.R
import com.netgpu.browser.compose.annotation.LightDarkPreview
import com.netgpu.browser.theme.NetGpuBrowserTheme

/**
 * Base component for buttons.
 *
 * @param text The button text to be displayed.
 * @param textColor [Color] to apply to the button text.
 * @param backgroundColor The background [Color] of the button.
 * @param icon Optional [Painter] used to display a [Icon] before the button text.
 * @param tint Tint [Color] to be applied to the icon.
 * @param onClick Invoked when the user clicks on the button.
 */
@Composable
private fun Button(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    icon: Painter? = null,
    tint: Color,
    onClick: () -> Unit,
) {
    androidx.compose.material.Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor,
        ),
    ) {
        icon?.let { painter ->
            Icon(
                painter = painter,
                contentDescription = null,
                tint = tint,
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            color = textColor,
            style = NetGpuBrowserTheme.typography.button,
            maxLines = 1,
        )
    }
}

/**
 * Primary button.
 *
 * @param text The button text to be displayed.
 * @param textColor [Color] to apply to the button text.
 * @param backgroundColor The background [Color] of the button.
 * @param icon Optional [Painter] used to display an [Icon] before the button text.
 * @param onClick Invoked when the user clicks on the button.
 */
@Composable
fun PrimaryButton(
    text: String,
    textColor: Color = NetGpuBrowserTheme.colors.textActionPrimary,
    backgroundColor: Color = NetGpuBrowserTheme.colors.actionPrimary,
    icon: Painter? = null,
    onClick: () -> Unit,
) {
    Button(
        text = text,
        textColor = textColor,
        backgroundColor = backgroundColor,
        icon = icon,
        tint = NetGpuBrowserTheme.colors.iconActionPrimary,
        onClick = onClick,
    )
}

/**
 * Secondary button.
 *
 * @param text The button text to be displayed.
 * @param textColor [Color] to apply to the button text.
 * @param backgroundColor The background [Color] of the button.
 * @param icon Optional [Painter] used to display an [Icon] before the button text.
 * @param onClick Invoked when the user clicks on the button.
 */
@Composable
fun SecondaryButton(
    text: String,
    textColor: Color = NetGpuBrowserTheme.colors.textActionSecondary,
    backgroundColor: Color = NetGpuBrowserTheme.colors.actionSecondary,
    icon: Painter? = null,
    onClick: () -> Unit,
) {
    Button(
        text = text,
        textColor = textColor,
        backgroundColor = backgroundColor,
        icon = icon,
        tint = NetGpuBrowserTheme.colors.iconActionSecondary,
        onClick = onClick,
    )
}

/**
 * Tertiary button.
 *
 * @param text The button text to be displayed.
 * @param textColor [Color] to apply to the button text.
 * @param backgroundColor The background [Color] of the button.
 * @param icon Optional [Painter] used to display an [Icon] before the button text.
 * @param onClick Invoked when the user clicks on the button.
 */
@Composable
fun TertiaryButton(
    text: String,
    textColor: Color = NetGpuBrowserTheme.colors.textActionTertiary,
    backgroundColor: Color = NetGpuBrowserTheme.colors.actionTertiary,
    icon: Painter? = null,
    onClick: () -> Unit,
) {
    Button(
        text = text,
        textColor = textColor,
        backgroundColor = backgroundColor,
        icon = icon,
        tint = NetGpuBrowserTheme.colors.iconActionTertiary,
        onClick = onClick,
    )
}

/**
 * Destructive button.
 *
 * @param text The button text to be displayed.
 * @param textColor [Color] to apply to the button text.
 * @param backgroundColor The background [Color] of the button.
 * @param icon Optional [Painter] used to display an [Icon] before the button text.
 * @param onClick Invoked when the user clicks on the button.
 */
@Composable
fun DestructiveButton(
    text: String,
    textColor: Color = NetGpuBrowserTheme.colors.textWarningButton,
    backgroundColor: Color = NetGpuBrowserTheme.colors.actionSecondary,
    icon: Painter? = null,
    onClick: () -> Unit,
) {
    Button(
        text = text,
        textColor = textColor,
        backgroundColor = backgroundColor,
        icon = icon,
        tint = NetGpuBrowserTheme.colors.iconWarningButton,
        onClick = onClick,
    )
}

@Composable
@LightDarkPreview
private fun ButtonPreview() {
    NetGpuBrowserTheme {
        Column(
            modifier = Modifier
                .background(NetGpuBrowserTheme.colors.layer1)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrimaryButton(
                text = "Label",
                icon = painterResource(R.drawable.ic_tab_collection),
                onClick = {},
            )

            SecondaryButton(
                text = "Label",
                icon = painterResource(R.drawable.ic_tab_collection),
                onClick = {},
            )

            TertiaryButton(
                text = "Label",
                icon = painterResource(R.drawable.ic_tab_collection),
                onClick = {},
            )

            DestructiveButton(
                text = "Label",
                icon = painterResource(R.drawable.ic_tab_collection),
                onClick = {},
            )
        }
    }
}
