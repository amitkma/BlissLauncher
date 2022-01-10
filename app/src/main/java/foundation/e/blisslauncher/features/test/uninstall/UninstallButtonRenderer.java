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

package foundation.e.blisslauncher.features.test.uninstall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import foundation.e.blisslauncher.R;

/** Created by falcon on 20/3/18. */
public class UninstallButtonRenderer {

  private static final float SIZE_PERCENTAGE = 0.3375f;

  private final Context mContext;
  private final int mSize;
  private final Paint mPaint =
      new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

  public UninstallButtonRenderer(Context context, int iconSizePx) {
    mContext = context;
    this.mSize = (int) (SIZE_PERCENTAGE * iconSizePx);
  }

  public void draw(Canvas canvas, Rect iconBounds) {
    Drawable uninstallDrawable =
        ResourcesCompat.getDrawable(
            mContext.getResources(), R.drawable.ic_minus_white_16dp, mContext.getTheme());
    uninstallDrawable.setBounds(
        iconBounds.right - mSize / 2,
        iconBounds.top - mSize / 2,
        iconBounds.right + mSize / 2,
        iconBounds.top + mSize / 2);
    uninstallDrawable.draw(canvas);
  }

  /**
   * We double the icons bounds here to increase the touch area of uninstall icon size.
   *
   * @param iconBounds
   * @return Doubled bounds for uninstall icon click.
   */
  public Rect getBoundsScaled(Rect iconBounds) {
    Rect uninstallIconBounds = new Rect();
    uninstallIconBounds.left = iconBounds.right - mSize;
    uninstallIconBounds.top = iconBounds.top - mSize;
    uninstallIconBounds.right = uninstallIconBounds.left + 2 * mSize;
    uninstallIconBounds.bottom = uninstallIconBounds.top + 2 * mSize;
    return uninstallIconBounds;
  }
}
