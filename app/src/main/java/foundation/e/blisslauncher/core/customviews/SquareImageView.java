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
import androidx.appcompat.widget.AppCompatImageView;
import foundation.e.blisslauncher.features.test.TestActivity;
import foundation.e.blisslauncher.features.test.VariantDeviceProfile;

/** Created by falcon on 16/2/18. */
public class SquareImageView extends AppCompatImageView {

  private VariantDeviceProfile deviceProfile;

  public SquareImageView(Context context) {
    this(context, null);
  }

  public SquareImageView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    TestActivity launcher = TestActivity.Companion.getLauncher(context);
    deviceProfile = launcher.getDeviceProfile();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int width = getMeasuredWidth();
    int height = getMeasuredHeight();
    int size = Math.min(width, height);
    setMeasuredDimension(deviceProfile.getIconSizePx(), deviceProfile.getIconSizePx());
  }
}
