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

package foundation.e.blisslauncher.uioverrides.touchcontrollers;

import static foundation.e.blisslauncher.core.Utilities.EDGE_NAV_BAR;
import static foundation.e.blisslauncher.features.test.LauncherState.NORMAL;
import static foundation.e.blisslauncher.features.test.LauncherState.OVERVIEW;

import android.view.MotionEvent;
import foundation.e.blisslauncher.core.customviews.AbstractFloatingView;
import foundation.e.blisslauncher.core.touch.AbstractStateChangeTouchController;
import foundation.e.blisslauncher.core.touch.SwipeDetector;
import foundation.e.blisslauncher.features.test.LauncherState;
import foundation.e.blisslauncher.features.test.LauncherStateManager;
import foundation.e.blisslauncher.features.test.TestActivity;
import foundation.e.blisslauncher.quickstep.RecentsModel;

/** Touch controller for handling edge swipes in landscape/seascape UI */
public class LandscapeEdgeSwipeController extends AbstractStateChangeTouchController {

  private static final String TAG = "LandscapeEdgeSwipeCtrl";

  public LandscapeEdgeSwipeController(TestActivity l) {
    super(l, SwipeDetector.HORIZONTAL);
  }

  @Override
  protected boolean canInterceptTouch(MotionEvent ev) {
    if (mCurrentAnimation != null) {
      // If we are already animating from a previous state, we can intercept.
      return true;
    }
    if (AbstractFloatingView.getTopOpenView(mLauncher) != null) {
      return false;
    }
    return mLauncher.isInState(NORMAL) && (ev.getEdgeFlags() & EDGE_NAV_BAR) != 0;
  }

  @Override
  protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
    boolean draggingFromNav = !isDragTowardPositive;
    return draggingFromNav ? OVERVIEW : NORMAL;
  }

  protected float getShiftRange() {
    return mLauncher.getDragLayer().getWidth();
  }

  @Override
  protected float initCurrentAnimation(
      @LauncherStateManager.AnimationComponents int animComponent) {
    float range = getShiftRange();
    long maxAccuracy = (long) (2 * range);
    mCurrentAnimation =
        mLauncher
            .getStateManager()
            .createAnimationToNewWorkspace(mToState, maxAccuracy, animComponent);
    return -2 / range;
  }

  @Override
  protected void onSwipeInteractionCompleted(LauncherState targetState) {
    super.onSwipeInteractionCompleted(targetState);
    if (mStartState == NORMAL && targetState == OVERVIEW) {
      RecentsModel.INSTANCE.get(mLauncher).onOverviewShown(true, TAG);
    }
  }
}
