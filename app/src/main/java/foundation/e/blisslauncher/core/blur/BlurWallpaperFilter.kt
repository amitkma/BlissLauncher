/*
 * Copyright (c) 2022 Amit Kumar.
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

package foundation.e.blisslauncher.core.blur

import android.content.Context
import android.graphics.Bitmap
import com.hoko.blur.HokoBlur
import com.hoko.blur.task.AsyncBlurTask

class BlurWallpaperFilter(private val context: Context) : WallpaperFilter {

    private var blurRadius = 8

    override fun apply(wallpaper: Bitmap): WallpaperFilter.ApplyTask {
        return WallpaperFilter.ApplyTask.create { emitter ->
            HokoBlur.with(context)
                .scheme(HokoBlur.SCHEME_NATIVE)
                .mode(HokoBlur.MODE_STACK)
                .radius(blurRadius)
                .sampleFactor(8f)
                .forceCopy(false)
                .needUpscale(true)
                .processor()
                .asyncBlur(
                    wallpaper,
                    object : AsyncBlurTask.Callback {
                        override fun onBlurSuccess(bitmap: Bitmap) {
                            emitter.onSuccess(bitmap)
                        }

                        override fun onBlurFailed(error: Throwable?) {
                            emitter.onError(error!!)
                        }
                    }
                )
        }
    }
}
