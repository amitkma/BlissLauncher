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

package foundation.e.blisslauncher.core.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.RelativeLayout;
import foundation.e.blisslauncher.BlissLauncher;
import foundation.e.blisslauncher.R;

public class InsettableRelativeLayout extends RelativeLayout {

  private final Context mContext;
  protected Rect mInsets;

  public InsettableRelativeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
  }

  @Override
  public WindowInsets onApplyWindowInsets(WindowInsets insets) {
    BlissLauncher.getApplication(mContext).resetDeviceProfile();
    mInsets.set(
        insets.getSystemWindowInsetLeft(),
        insets.getSystemWindowInsetTop(),
        insets.getSystemWindowInsetRight(),
        insets.getSystemWindowInsetBottom());
    updateChildInsets(mInsets);
    return insets;
  }

  private void updateChildInsets(Rect insets) {
    if (insets == null) return;
    int childCount = getChildCount();
    for (int index = 0; index < childCount; ++index) {
      View child = getChildAt(index);
      if (child instanceof Insettable) {
        ((Insettable) child).setInsets(insets);
      }
    }
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new InsettableRelativeLayout.LayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  // Override to allow type-checking of LayoutParams.
  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof InsettableRelativeLayout.LayoutParams;
  }

  @Override
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  public static class LayoutParams extends RelativeLayout.LayoutParams {
    public boolean ignoreInsets = false;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
      TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.InsettableFrameLayout_Layout);
      ignoreInsets =
          a.getBoolean(R.styleable.InsettableFrameLayout_Layout_layout_ignoreInsets, false);
      a.recycle();
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams lp) {
      super(lp);
    }
  }

  @Override
  public void onViewAdded(View child) {
    super.onViewAdded(child);
    updateChildInsets(mInsets);
  }
}
