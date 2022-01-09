/*
 * Copyright 2022 Amit Kumar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foundation.e.blisslauncher.core.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.GridLayout
import foundation.e.blisslauncher.BlissLauncher
import foundation.e.blisslauncher.core.blur.BlurWallpaperProvider
import foundation.e.blisslauncher.core.blur.ShaderBlurDrawable
import foundation.e.blisslauncher.core.runOnMainThread

class DockGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr),
    Insettable,
    BlurWallpaperProvider.Listener {
    private val blurWallpaperProvider: BlurWallpaperProvider
    private var fullBlurDrawable: ShaderBlurDrawable? = null
    private val blurAlpha = 255
    private val blurDrawableCallback: Drawable.Callback = object : Drawable.Callback {
        override fun invalidateDrawable(who: Drawable) {
            runOnMainThread {
                invalidate()
                null
            }
        }

        override fun scheduleDrawable(
            who: Drawable,
            what: Runnable,
            `when`: Long
        ) {
        }

        override fun unscheduleDrawable(
            who: Drawable,
            what: Runnable
        ) {
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        BlurWallpaperProvider.getInstance(context).addListener(this)
        fullBlurDrawable!!.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        BlurWallpaperProvider.getInstance(context).removeListener(this)
        fullBlurDrawable!!.stopListening()
    }

    override fun onDraw(canvas: Canvas) {
        fullBlurDrawable!!.alpha = blurAlpha
        fullBlurDrawable!!.draw(canvas)
        super.onDraw(canvas)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            fullBlurDrawable!!.setBounds(left, top, right, bottom)
        }
    }

    override fun setInsets(insets: Rect) {
        if (insets == null) return
        val deviceProfile = BlissLauncher.getApplication(context).deviceProfile
        val lp =
            layoutParams as InsettableRelativeLayout.LayoutParams
        lp.height = deviceProfile.hotseatCellHeightPx + insets.bottom
        setPadding(
            deviceProfile.iconDrawablePaddingPx / 2, 0,
            deviceProfile.iconDrawablePaddingPx / 2, insets.bottom
        )
        layoutParams = lp
    }

    private fun createBlurDrawable() {
        if (isAttachedToWindow) {
            fullBlurDrawable!!.stopListening()
        }
        fullBlurDrawable = blurWallpaperProvider.createDrawable()
        fullBlurDrawable!!.callback = blurDrawableCallback
        fullBlurDrawable!!.setBounds(left, top, right, bottom)
        if (isAttachedToWindow) fullBlurDrawable!!.startListening()
    }

    override fun onEnabledChanged() {
        createBlurDrawable()
    }

    override fun onWallpaperChanged() {}

    init {
        setWillNotDraw(false)
        blurWallpaperProvider = BlurWallpaperProvider.getInstance(context)
        createBlurDrawable()
    }
}
