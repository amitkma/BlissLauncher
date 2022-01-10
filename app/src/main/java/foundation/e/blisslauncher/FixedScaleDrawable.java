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

package foundation.e.blisslauncher;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Build;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/** Extension of {@link DrawableWrapper} which scales the child drawables by a fixed amount. */
@TargetApi(Build.VERSION_CODES.N)
public class FixedScaleDrawable extends DrawableWrapper {

  // TODO b/33553066 use the constant defined in MaskableIconDrawable
  public static final float LEGACY_ICON_SCALE = .7f * .6667f;
  private float mScaleX, mScaleY;

  public FixedScaleDrawable() {
    super(new ColorDrawable());
    mScaleX = LEGACY_ICON_SCALE;
    mScaleY = LEGACY_ICON_SCALE;
  }

  @Override
  public void draw(Canvas canvas) {
    int saveCount = canvas.save();
    canvas.scale(mScaleX, mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
    super.draw(canvas);
    canvas.restoreToCount(saveCount);
  }

  @Override
  public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) {}

  @Override
  public void inflate(
      Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {}

  public void setScale(float scale) {
    float h = getIntrinsicHeight();
    float w = getIntrinsicWidth();
    mScaleX = scale * LEGACY_ICON_SCALE;
    mScaleY = scale * LEGACY_ICON_SCALE;
    if (h > w && w > 0) {
      mScaleX *= w / h;
    } else if (w > h && h > 0) {
      mScaleY *= h / w;
    }
  }
}
