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

package foundation.e.blisslauncher.features.test;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.Utilities;

/** Manages the opening and closing app transitions from Launcher. */
public class LauncherAppTransitionManager {

  public static LauncherAppTransitionManager newInstance(Context context) {
    return Utilities.getOverrideObject(
        LauncherAppTransitionManager.class, context, R.string.app_transition_manager_class);
  }

  public ActivityOptions getActivityLaunchOptions(TestActivity launcher, View v) {
    int left = 0, top = 0;
    int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
    if (v instanceof IconTextView) {
      // Launch from center of icon, not entire view
      Drawable icon = ((IconTextView) v).getIcon();
      if (icon != null) {
        Rect bounds = icon.getBounds();
        left = (width - bounds.width()) / 2;
        top = v.getPaddingTop();
        width = bounds.width();
        height = bounds.height();
      }
    }
    return ActivityOptions.makeClipRevealAnimation(v, left, top, width, height);
  }

  /** Number of animations which run on state properties. */
  public int getStateElementAnimationsCount() {
    return 0;
  }

  public Animator createStateElementAnimation(int index, float... values) {
    throw new RuntimeException("Unknown gesture animation " + index);
  }
}
