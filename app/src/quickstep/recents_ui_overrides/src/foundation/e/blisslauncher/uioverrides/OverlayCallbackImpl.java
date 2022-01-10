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

package foundation.e.blisslauncher.uioverrides;

import foundation.e.blisslauncher.features.test.TestActivity;
import org.jetbrains.annotations.Nullable;

public class OverlayCallbackImpl implements TestActivity.LauncherOverlay {

  private final TestActivity mLauncher;
  private static final String TAG = "OverlayCallbackImpl";
  private float mProgress = 0;
  private boolean scrollFromWorkspace = false;

  private TestActivity.LauncherOverlayCallbacks mLauncherOverlayCallbacks;

  // The page is moved more than halfway, automatically move to the next page on touch up.
  private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;

  public OverlayCallbackImpl(TestActivity launcher) {
    this.mLauncher = launcher;
  }

  @Override
  public void onScrollInteractionBegin() {
    mLauncherOverlayCallbacks.onScrollBegin();
  }

  @Override
  public void onScrollInteractionEnd() {
    if (scrollFromWorkspace) {
      if (mProgress >= SIGNIFICANT_MOVE_THRESHOLD) mLauncherOverlayCallbacks.onScrollEnd(1f, true);
      else mLauncherOverlayCallbacks.onScrollEnd(0f, true);
    } else {
      if (mProgress < SIGNIFICANT_MOVE_THRESHOLD) mLauncherOverlayCallbacks.onScrollEnd(0f, false);
      else mLauncherOverlayCallbacks.onScrollEnd(1f, false);
    }
  }

  @Override
  public void onScrollChange(float progress, boolean scrollFromWorkspace, boolean rtl) {
    if (mLauncherOverlayCallbacks != null) {
      mLauncherOverlayCallbacks.onScrollChanged(progress, scrollFromWorkspace);
      mProgress = progress;
      this.scrollFromWorkspace = scrollFromWorkspace;
    }
  }

  @Override
  public void setOverlayCallbacks(@Nullable TestActivity.LauncherOverlayCallbacks callbacks) {
    mLauncherOverlayCallbacks = callbacks;
  }
}
