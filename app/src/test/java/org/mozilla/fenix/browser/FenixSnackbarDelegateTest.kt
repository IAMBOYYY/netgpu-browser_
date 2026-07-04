/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.netgpu.browser.browser

import android.view.View
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.netgpu.browser.R
import com.netgpu.browser.components.NetGpuBrowserSnackbar
import com.netgpu.browser.components.NetGpuBrowserSnackbar.Companion.LENGTH_SHORT
import com.netgpu.browser.helpers.MockkRetryTestRule

class NetGpuBrowserSnackbarDelegateTest {

    @MockK private lateinit var view: View

    @MockK(relaxed = true)
    private lateinit var snackbar: NetGpuBrowserSnackbar
    private lateinit var delegate: NetGpuBrowserSnackbarDelegate

    @get:Rule
    val mockkRule = MockkRetryTestRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkObject(NetGpuBrowserSnackbar.Companion)

        delegate = NetGpuBrowserSnackbarDelegate(view)
        every {
            NetGpuBrowserSnackbar.make(view, LENGTH_SHORT, isDisplayedWithBrowserToolbar = true)
        } returns snackbar
        every { snackbar.setText(any()) } returns snackbar
        every { snackbar.setAction(any(), any()) } returns snackbar
        every { view.context.getString(R.string.app_name) } returns "NETGPU BROWSER"
        every { view.context.getString(R.string.edit) } returns "Edit"
    }

    @After
    fun teardown() {
        unmockkObject(NetGpuBrowserSnackbar.Companion)
    }

    @Test
    fun `show with no listener nor action`() {
        delegate.show(
            snackBarParentView = mockk(),
            text = R.string.app_name,
            duration = 0,
            action = 0,
            listener = null,
        )

        verify { snackbar.setText("NETGPU BROWSER") }
        verify(exactly = 0) { snackbar.setAction(any(), any()) }
        verify { snackbar.show() }
    }

    @Test
    fun `show with listener but no action`() {
        delegate.show(
            snackBarParentView = mockk(),
            text = R.string.app_name,
            duration = 0,
            action = 0,
            listener = {},
        )

        verify { snackbar.setText("NETGPU BROWSER") }
        verify(exactly = 0) { snackbar.setAction(any(), any()) }
        verify { snackbar.show() }
    }

    @Test
    fun `show with action but no listener`() {
        delegate.show(
            snackBarParentView = mockk(),
            text = R.string.app_name,
            duration = 0,
            action = R.string.edit,
            listener = null,
        )

        verify { snackbar.setText("NETGPU BROWSER") }
        verify(exactly = 0) { snackbar.setAction(any(), any()) }
        verify { snackbar.show() }
    }

    @Test
    fun `show with listener and action`() {
        val listener = mockk<(View) -> Unit>(relaxed = true)
        delegate.show(
            snackBarParentView = mockk(),
            text = R.string.app_name,
            duration = 0,
            action = R.string.edit,
            listener = listener,
        )

        verify { snackbar.setText("NETGPU BROWSER") }
        verify {
            snackbar.setAction(
                "Edit",
                withArg {
                    verify(exactly = 0) { listener(view) }
                    it.invoke()
                    verify { listener(view) }
                },
            )
        }
        verify { snackbar.show() }
    }
}
