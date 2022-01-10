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
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import foundation.e.blisslauncher.BlissLauncher;
import foundation.e.blisslauncher.core.DeviceProfile;

public class PageIndicatorLinearLayout extends LinearLayout {
  private Context mContext;

  public PageIndicatorLinearLayout(Context context) {
    this(context, null);
  }

  public PageIndicatorLinearLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PageIndicatorLinearLayout(
      Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    DeviceProfile deviceProfile = BlissLauncher.getApplication(mContext).getDeviceProfile();
    setMeasuredDimension(
        deviceProfile.getAvailableWidthPx(), deviceProfile.getPageIndicatorHeight());
  }
}
