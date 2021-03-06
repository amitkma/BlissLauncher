/*
 * Copyright (c) 2018 Amit Kumar.
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

package foundation.e.blisslauncher.quickstep.fallback;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.WindowInsets;
import foundation.e.blisslauncher.features.test.BaseActivity;
import foundation.e.blisslauncher.features.test.BaseDragLayer;
import foundation.e.blisslauncher.features.test.TouchController;
import foundation.e.blisslauncher.quickstep.RecentsActivity;
import org.jetbrains.annotations.Nullable;

public class RecentsRootView extends BaseDragLayer<RecentsActivity> {

  private static final int MIN_SIZE = 10;
  private final RecentsActivity mActivity;

  private final Point mLastKnownSize = new Point(MIN_SIZE, MIN_SIZE);

  public RecentsRootView(Context context, AttributeSet attrs) {
    super(context, attrs, 1 /* alphaChannelCount */);
    mActivity = (RecentsActivity) BaseActivity.fromContext(context);
    setSystemUiVisibility(
        SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  public Point getLastKnownSize() {
    return mLastKnownSize;
  }

  public void setup() {
    mControllers = new TouchController[] {new RecentsTaskController(mActivity)};
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Check size changes before the actual measure, to avoid multiple measure calls.
    int width = Math.max(MIN_SIZE, MeasureSpec.getSize(widthMeasureSpec));
    int height = Math.max(MIN_SIZE, MeasureSpec.getSize(heightMeasureSpec));
    if (mLastKnownSize.x != width || mLastKnownSize.y != height) {
      mLastKnownSize.set(width, height);
      mActivity.onRootViewSizeChanged();
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Nullable
  @Override
  public WindowInsets onApplyWindowInsets(@Nullable WindowInsets insets) {
    // Update device profile before notifying the children.
    Rect tmpInsets = new Rect();
    if (insets != null) {
      tmpInsets.set(
          insets.getSystemWindowInsetLeft(),
          insets.getSystemWindowInsetTop(),
          insets.getSystemWindowInsetRight(),
          insets.getSystemWindowInsetBottom());
    }
    mActivity.getDeviceProfile().updateInsets(tmpInsets);
    setInsets(tmpInsets);
    return insets;
  }

  @Override
  public void setInsets(Rect insets) {
    // If the insets haven't changed, this is a no-op. Avoid unnecessary layout caused by
    // modifying child layout params.
    if (!insets.equals(mInsets)) {
      super.setInsets(insets);
    }
  }

  public void dispatchInsets() {
    mActivity.getDeviceProfile().updateInsets(mInsets);
    super.setInsets(mInsets);
  }
}
