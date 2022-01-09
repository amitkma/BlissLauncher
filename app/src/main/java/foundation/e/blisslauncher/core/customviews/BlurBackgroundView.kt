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
import android.view.View
import foundation.e.blisslauncher.core.blur.BlurWallpaperProvider
import foundation.e.blisslauncher.core.blur.ShaderBlurDrawable
import foundation.e.blisslauncher.core.runOnMainThread

class BlurBackgroundView(context: Context, attrs: AttributeSet?) :
    View(context, attrs),
    Insettable,
    BlurWallpaperProvider.Listener {

    private val blurWallpaperProvider by lazy { BlurWallpaperProvider.getInstance(context) }

    private var fullBlurDrawable: ShaderBlurDrawable? = null
    private var blurAlpha = 255

    private val blurDrawableCallback by lazy {
        object : Drawable.Callback {
            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            }

            override fun invalidateDrawable(who: Drawable) {
                runOnMainThread { invalidate() }
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            }
        }
    }

    init {
        createFullBlurDrawable()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        BlurWallpaperProvider.getInstance(context).addListener(this)
        fullBlurDrawable?.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        BlurWallpaperProvider.getInstance(context).removeListener(this)
        fullBlurDrawable?.stopListening()
    }

    override fun onDraw(canvas: Canvas) {
        fullBlurDrawable?.apply {
            alpha = blurAlpha
            this.draw(canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            fullBlurDrawable?.setBounds(left, top, right, bottom)
        }
    }

    private fun createFullBlurDrawable() {
        fullBlurDrawable?.let { if (isAttachedToWindow) it.stopListening() }
        fullBlurDrawable = blurWallpaperProvider.createDrawable().apply {
            callback = blurDrawableCallback
            setBounds(left, top, right, bottom)
            if (isAttachedToWindow) startListening()
        }
    }

    override fun onEnabledChanged() {
        createFullBlurDrawable()
    }

    override fun setInsets(insets: Rect) {}
}
