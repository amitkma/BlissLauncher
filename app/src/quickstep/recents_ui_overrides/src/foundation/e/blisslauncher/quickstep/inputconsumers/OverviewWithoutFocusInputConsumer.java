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

package foundation.e.blisslauncher.quickstep.inputconsumers;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static com.android.systemui.shared.system.ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS;
import static foundation.e.blisslauncher.core.Utilities.squaredHypot;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;
import foundation.e.blisslauncher.core.Utilities;
import foundation.e.blisslauncher.quickstep.OverviewCallbacks;
import foundation.e.blisslauncher.quickstep.util.NavBarPosition;

public class OverviewWithoutFocusInputConsumer implements InputConsumer {

  private final InputMonitorCompat mInputMonitor;
  private final boolean mDisableHorizontalSwipe;
  private final PointF mDownPos = new PointF();
  private final float mSquaredTouchSlop;
  private final Context mContext;
  private final NavBarPosition mNavBarPosition;

  private boolean mInterceptedTouch;
  private VelocityTracker mVelocityTracker;

  public OverviewWithoutFocusInputConsumer(
      Context context, InputMonitorCompat inputMonitor, boolean disableHorizontalSwipe) {
    mInputMonitor = inputMonitor;
    mDisableHorizontalSwipe = disableHorizontalSwipe;
    mContext = context;
    mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
    mNavBarPosition = new NavBarPosition(context);

    mVelocityTracker = VelocityTracker.obtain();
  }

  @Override
  public int getType() {
    return TYPE_OVERVIEW_WITHOUT_FOCUS;
  }

  @Override
  public boolean allowInterceptByParent() {
    return !mInterceptedTouch;
  }

  private void endTouchTracking() {
    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }

  @Override
  public void onMotionEvent(MotionEvent ev) {
    if (mVelocityTracker == null) {
      return;
    }

    mVelocityTracker.addMovement(ev);
    switch (ev.getActionMasked()) {
      case ACTION_DOWN:
        {
          mDownPos.set(ev.getX(), ev.getY());
          break;
        }
      case ACTION_MOVE:
        {
          if (!mInterceptedTouch) {
            float displacementX = ev.getX() - mDownPos.x;
            float displacementY = ev.getY() - mDownPos.y;
            if (squaredHypot(displacementX, displacementY) >= mSquaredTouchSlop) {
              if (mDisableHorizontalSwipe && Math.abs(displacementX) > Math.abs(displacementY)) {
                // Horizontal gesture is not allowed in this region
                endTouchTracking();
                break;
              }

              mInterceptedTouch = true;

              if (mInputMonitor != null) {
                mInputMonitor.pilferPointers();
              }
            }
          }
          break;
        }

      case ACTION_CANCEL:
        endTouchTracking();
        break;

      case ACTION_UP:
        {
          finishTouchTracking(ev);
          endTouchTracking();
          break;
        }
    }
  }

  private void finishTouchTracking(MotionEvent ev) {
    mVelocityTracker.computeCurrentVelocity(100);
    float velocityX = mVelocityTracker.getXVelocity();
    float velocityY = mVelocityTracker.getYVelocity();
    float velocity =
        mNavBarPosition.isRightEdge()
            ? -velocityX
            : (mNavBarPosition.isLeftEdge() ? velocityX : -velocityY);

    final boolean triggerQuickstep;
    if (Math.abs(velocity) >= ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity()) {
      triggerQuickstep = velocity > 0;
    } else {
      float displacementX = mDisableHorizontalSwipe ? 0 : (ev.getX() - mDownPos.x);
      float displacementY = ev.getY() - mDownPos.y;
      triggerQuickstep = squaredHypot(displacementX, displacementY) >= mSquaredTouchSlop;
    }

    if (triggerQuickstep) {
      OverviewCallbacks.get(mContext).closeAllWindows();
      ActivityManagerWrapper.getInstance().closeSystemWindows(CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
    } else {
      // ignore
    }
  }
}
