/*
 * Copyright (c) 2019 Amit Kumar.
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

import static foundation.e.blisslauncher.uioverrides.touchcontrollers.PortraitStatesTouchController.isTouchOverHotseat;

import android.view.MotionEvent;
import foundation.e.blisslauncher.features.test.TestActivity;
import foundation.e.blisslauncher.features.test.anim.PendingAnimation;
import foundation.e.blisslauncher.quickstep.views.RecentsView;
import foundation.e.blisslauncher.quickstep.views.TaskView;

/**
 * Helper class for {@link PortraitStatesTouchController} that determines swipeable regions and
 * animations on the overview state that depend on the recents implementation.
 */
public final class PortraitOverviewStateTouchHelper {

  RecentsView mRecentsView;
  TestActivity mLauncher;

  public PortraitOverviewStateTouchHelper(TestActivity launcher) {
    mLauncher = launcher;
    mRecentsView = launcher.getOverviewPanel();
  }

  /**
   * Whether or not {@link PortraitStatesTouchController} should intercept the touch when on the
   * overview state.
   *
   * @param ev the motion event
   * @return true if we should intercept the motion event
   */
  boolean canInterceptTouch(MotionEvent ev) {
    if (mRecentsView.getChildCount() > 0) {
      // Allow swiping up in the gap between the hotseat and overview.
      return ev.getY() >= mRecentsView.getChildAt(0).getBottom();
    } else {
      // If there are no tasks, we only intercept if we're below the hotseat height.
      return isTouchOverHotseat(mLauncher, ev);
    }
  }

  /**
   * Whether or not swiping down to leave overview state should return to the currently running task
   * app.
   *
   * @return true if going back should take the user to the currently running task
   */
  boolean shouldSwipeDownReturnToApp() {
    TaskView taskView = mRecentsView.getTaskViewAt(mRecentsView.getNextPage());
    return taskView != null && mRecentsView.shouldSwipeDownLaunchApp();
  }

  /**
   * Create the animation for going from overview to the task app via swiping. Should only be called
   * when {@link #shouldSwipeDownReturnToApp()} returns true.
   *
   * @param duration how long the animation should be
   * @return the animation
   */
  PendingAnimation createSwipeDownToTaskAppAnimation(long duration) {
    mRecentsView.setCurrentPage(mRecentsView.getPageNearestToCenterOfScreen());
    TaskView taskView = mRecentsView.getTaskViewAt(mRecentsView.getCurrentPage());
    if (taskView == null) {
      throw new IllegalStateException("There is no task view to animate to.");
    }
    return mRecentsView.createTaskLauncherAnimation(taskView, duration);
  }
}
