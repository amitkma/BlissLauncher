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
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static foundation.e.blisslauncher.core.Utilities.squaredHypot;
import static foundation.e.blisslauncher.core.Utilities.squaredTouchSlop;
import static foundation.e.blisslauncher.quickstep.MultiStateCallback.DEBUG_STATES;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.BackgroundExecutor;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.Utilities;
import foundation.e.blisslauncher.quickstep.LockScreenRecentsActivity;
import foundation.e.blisslauncher.quickstep.MultiStateCallback;
import foundation.e.blisslauncher.quickstep.SwipeSharedState;
import foundation.e.blisslauncher.quickstep.WindowTransformSwipeHandler;
import foundation.e.blisslauncher.quickstep.util.ClipAnimationHelper;
import foundation.e.blisslauncher.quickstep.util.RecentsAnimationListenerSet;
import foundation.e.blisslauncher.quickstep.util.SwipeAnimationTargetSet;

/** A dummy input consumer used when the device is still locked, e.g. from secure camera. */
public class DeviceLockedInputConsumer
    implements InputConsumer, SwipeAnimationTargetSet.SwipeAnimationListener {

  private static final float SCALE_DOWN = 0.75f;

  private static final String[] STATE_NAMES = DEBUG_STATES ? new String[2] : null;

  private static int getFlagForIndex(int index, String name) {
    if (DEBUG_STATES) {
      STATE_NAMES[index] = name;
    }
    return 1 << index;
  }

  private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
  private static final int STATE_HANDLER_INVALIDATED =
      getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");

  private final Context mContext;
  private final float mTouchSlopSquared;
  private final SwipeSharedState mSwipeSharedState;
  private final InputMonitorCompat mInputMonitorCompat;

  private final PointF mTouchDown = new PointF();
  private final ClipAnimationHelper mClipAnimationHelper;
  private final ClipAnimationHelper.TransformParams mTransformParams;
  private final Point mDisplaySize;
  private final MultiStateCallback mStateCallback;
  private final RectF mSwipeTouchRegion;
  public final int mRunningTaskId;

  private VelocityTracker mVelocityTracker;
  private float mProgress;

  private boolean mThresholdCrossed = false;

  private SwipeAnimationTargetSet mTargetSet;

  public DeviceLockedInputConsumer(
      Context context,
      SwipeSharedState swipeSharedState,
      InputMonitorCompat inputMonitorCompat,
      RectF swipeTouchRegion,
      int runningTaskId) {
    mContext = context;
    mTouchSlopSquared = squaredTouchSlop(context);
    mSwipeSharedState = swipeSharedState;
    mClipAnimationHelper = new ClipAnimationHelper(context);
    mTransformParams = new ClipAnimationHelper.TransformParams();
    mInputMonitorCompat = inputMonitorCompat;
    mSwipeTouchRegion = swipeTouchRegion;
    mRunningTaskId = runningTaskId;

    // Do not use DeviceProfile as the user data might be locked
    mDisplaySize = new Point();
    context.getSystemService(WindowManager.class).getDefaultDisplay().getRealSize(mDisplaySize);

    // Init states
    mStateCallback = new MultiStateCallback(STATE_NAMES);
    mStateCallback.addCallback(
        STATE_TARGET_RECEIVED | STATE_HANDLER_INVALIDATED, this::endRemoteAnimation);

    mVelocityTracker = VelocityTracker.obtain();
  }

  @Override
  public int getType() {
    return TYPE_DEVICE_LOCKED;
  }

  @Override
  public void onMotionEvent(MotionEvent ev) {
    if (mVelocityTracker == null) {
      return;
    }
    mVelocityTracker.addMovement(ev);

    float x = ev.getX();
    float y = ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mTouchDown.set(x, y);
        break;
      case ACTION_POINTER_DOWN:
        {
          if (!mThresholdCrossed) {
            // Cancel interaction in case of multi-touch interaction
            int ptrIdx = ev.getActionIndex();
            if (!mSwipeTouchRegion.contains(ev.getX(ptrIdx), ev.getY(ptrIdx))) {
              int action = ev.getAction();
              ev.setAction(ACTION_CANCEL);
              finishTouchTracking(ev);
              ev.setAction(action);
            }
          }
          break;
        }
      case MotionEvent.ACTION_MOVE:
        {
          if (!mThresholdCrossed) {
            if (squaredHypot(x - mTouchDown.x, y - mTouchDown.y) > mTouchSlopSquared) {
              startRecentsTransition();
            }
          } else {
            float dy = Math.max(mTouchDown.y - y, 0);
            mProgress = dy / mDisplaySize.y;
            mTransformParams.setProgress(mProgress);
            if (mTargetSet != null) {
              mClipAnimationHelper.applyTransform(mTargetSet, mTransformParams);
            }
          }
          break;
        }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        finishTouchTracking(ev);
        break;
    }
  }

  /**
   * Called when the gesture has ended. Does not correlate to the completion of the interaction as
   * the animation can still be running.
   */
  private void finishTouchTracking(MotionEvent ev) {
    mStateCallback.setState(STATE_HANDLER_INVALIDATED);
    if (mThresholdCrossed && ev.getAction() == ACTION_UP) {
      mVelocityTracker.computeCurrentVelocity(
          1000, ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity());

      float velocityY = mVelocityTracker.getYVelocity();
      float flingThreshold =
          mContext.getResources().getDimension(R.dimen.quickstep_fling_threshold_velocity);

      boolean dismissTask;
      if (Math.abs(velocityY) > flingThreshold) {
        // Is fling
        dismissTask = velocityY < 0;
      } else {
        dismissTask = mProgress >= (1 - WindowTransformSwipeHandler.MIN_PROGRESS_FOR_OVERVIEW);
      }
      if (dismissTask) {
        // For now, just start the home intent so user is prompted to unlock the device.
        mContext.startActivity(
            new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      }
    }
    mVelocityTracker.recycle();
    mVelocityTracker = null;
  }

  private void startRecentsTransition() {
    mThresholdCrossed = true;
    RecentsAnimationListenerSet newListenerSet = mSwipeSharedState.newRecentsAnimationListenerSet();
    newListenerSet.addListener(this);
    Intent intent =
        new Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setComponent(new ComponentName(mContext, LockScreenRecentsActivity.class))
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    mInputMonitorCompat.pilferPointers();
    BackgroundExecutor.get()
        .submit(
            () ->
                ActivityManagerWrapper.getInstance()
                    .startRecentsActivity(intent, null, newListenerSet, null, null));
  }

  @Override
  public void onRecentsAnimationStart(SwipeAnimationTargetSet targetSet) {
    mTargetSet = targetSet;

    Rect displaySize = new Rect(0, 0, mDisplaySize.x, mDisplaySize.y);
    RemoteAnimationTargetCompat targetCompat = targetSet.findTask(mRunningTaskId);
    if (targetCompat != null) {
      mClipAnimationHelper.updateSource(displaySize, targetCompat);
    }

    Utilities.scaleRectAboutCenter(displaySize, SCALE_DOWN);
    displaySize.offsetTo(displaySize.left, 0);
    mClipAnimationHelper.updateTargetRect(displaySize);
    mClipAnimationHelper.applyTransform(mTargetSet, mTransformParams);

    mStateCallback.setState(STATE_TARGET_RECEIVED);
  }

  @Override
  public void onRecentsAnimationCanceled() {
    mTargetSet = null;
  }

  private void endRemoteAnimation() {
    if (mTargetSet != null) {
      mTargetSet.finishController(
          false /* toRecents */, null /* callback */, false /* sendUserLeaveHint */);
    }
  }

  @Override
  public void onConsumerAboutToBeSwitched() {
    mStateCallback.setState(STATE_HANDLER_INVALIDATED);
  }

  @Override
  public boolean allowInterceptByParent() {
    return !mThresholdCrossed;
  }
}
