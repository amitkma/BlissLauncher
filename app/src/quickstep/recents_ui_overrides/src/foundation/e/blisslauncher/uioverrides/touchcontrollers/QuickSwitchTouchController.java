/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foundation.e.blisslauncher.uioverrides.touchcontrollers;

import static com.android.blisslauncher.LauncherState.NORMAL;
import static com.android.blisslauncher.LauncherState.QUICK_SWITCH;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_ALL_APPS_FADE;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_OVERVIEW_FADE;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_OVERVIEW_SCALE;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_OVERVIEW_TRANSLATE_Y;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_VERTICAL_PROGRESS;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_WORKSPACE_FADE;
import static com.android.blisslauncher.anim.AnimatorSetBuilder.ANIM_WORKSPACE_TRANSLATE;
import static com.android.blisslauncher.anim.Interpolators.ACCEL_2;
import static com.android.blisslauncher.anim.Interpolators.DEACCEL_2;
import static com.android.blisslauncher.anim.Interpolators.INSTANT;
import static com.android.blisslauncher.anim.Interpolators.LINEAR;
import static com.android.blisslauncher.util.SystemUiController.UI_STATE_OVERVIEW;
import static com.android.systemui.shared.system.QuickStepContract.SYSUI_STATE_OVERVIEW_DISABLED;

import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.android.blisslauncher.Launcher;
import com.android.blisslauncher.LauncherState;
import com.android.blisslauncher.LauncherStateManager;
import com.android.blisslauncher.Utilities;
import com.android.blisslauncher.anim.AnimatorSetBuilder;
import com.android.blisslauncher.touch.AbstractStateChangeTouchController;
import com.android.blisslauncher.touch.SwipeDetector;
import com.android.blisslauncher.userevent.nano.LauncherLogProto;
import com.android.blisslauncher.userevent.nano.LauncherLogProto.Action.Direction;
import foundation.e.quickstep.OverviewInteractionState;
import foundation.e.quickstep.SysUINavigationMode;
import foundation.e.quickstep.SysUINavigationMode.Mode;
import foundation.e.quickstep.views.RecentsView;
import foundation.e.quickstep.views.TaskView;

/**
 * Handles quick switching to a recent task from the home screen.
 */
public class QuickSwitchTouchController extends AbstractStateChangeTouchController {

    private @Nullable
    TaskView mTaskToLaunch;

    public QuickSwitchTouchController(Launcher launcher) {
        this(launcher, SwipeDetector.HORIZONTAL);
    }

    protected QuickSwitchTouchController(Launcher l, SwipeDetector.Direction dir) {
        super(l, dir);
    }

    @Override
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (mCurrentAnimation != null) {
            return true;
        }
        if (!mLauncher.isInState(LauncherState.NORMAL)) {
            return false;
        }
        if ((ev.getEdgeFlags() & Utilities.EDGE_NAV_BAR) == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        int stateFlags = OverviewInteractionState.INSTANCE.get(mLauncher).getSystemUiStateFlags();
        if ((stateFlags & SYSUI_STATE_OVERVIEW_DISABLED) != 0) {
            return NORMAL;
        }
        return isDragTowardPositive ? QUICK_SWITCH : NORMAL;
    }

    @Override
    public void onDragStart(boolean start) {
        super.onDragStart(start);
        mStartContainerType = LauncherLogProto.ContainerType.NAVBAR;
        mTaskToLaunch = mLauncher.<RecentsView>getOverviewPanel().getTaskViewAt(0);
    }

    @Override
    protected void onSwipeInteractionCompleted(LauncherState targetState, int logAction) {
        super.onSwipeInteractionCompleted(targetState, logAction);
        mTaskToLaunch = null;
    }

    @Override
    protected float initCurrentAnimation(int animComponents) {
        AnimatorSetBuilder animatorSetBuilder = new AnimatorSetBuilder();
        setupInterpolators(animatorSetBuilder);
        long accuracy = (long) (getShiftRange() * 2);
        mCurrentAnimation = mLauncher.getStateManager().createAnimationToNewWorkspace(mToState,
                animatorSetBuilder, accuracy, this::clearState, LauncherStateManager.ANIM_ALL);
        mCurrentAnimation.getAnimationPlayer().addUpdateListener(valueAnimator -> {
            updateFullscreenProgress((Float) valueAnimator.getAnimatedValue());
        });
        return 1 / getShiftRange();
    }

    private void setupInterpolators(AnimatorSetBuilder animatorSetBuilder) {
        animatorSetBuilder.setInterpolator(ANIM_WORKSPACE_FADE, DEACCEL_2);
        animatorSetBuilder.setInterpolator(ANIM_ALL_APPS_FADE, DEACCEL_2);
        if (SysUINavigationMode.getMode(mLauncher) == Mode.NO_BUTTON) {
            // Overview lives to the left of workspace, so translate down later than over
            animatorSetBuilder.setInterpolator(ANIM_WORKSPACE_TRANSLATE, ACCEL_2);
            animatorSetBuilder.setInterpolator(ANIM_VERTICAL_PROGRESS, ACCEL_2);
            animatorSetBuilder.setInterpolator(ANIM_OVERVIEW_SCALE, ACCEL_2);
            animatorSetBuilder.setInterpolator(ANIM_OVERVIEW_TRANSLATE_Y, ACCEL_2);
            animatorSetBuilder.setInterpolator(ANIM_OVERVIEW_FADE, INSTANT);
        } else {
            animatorSetBuilder.setInterpolator(ANIM_WORKSPACE_TRANSLATE, LINEAR);
            animatorSetBuilder.setInterpolator(ANIM_VERTICAL_PROGRESS, LINEAR);
        }
    }

    @Override
    protected void updateProgress(float progress) {
        super.updateProgress(progress);
        updateFullscreenProgress(Utilities.boundToRange(progress, 0, 1));
    }

    private void updateFullscreenProgress(float progress) {
        if (mTaskToLaunch != null) {
            mTaskToLaunch.setFullscreenProgress(progress);
            int sysuiFlags = progress > RecentsView.UPDATE_SYSUI_FLAGS_THRESHOLD
                    ? mTaskToLaunch.getThumbnail().getSysUiStatusNavFlags()
                    : 0;
            mLauncher.getSystemUiController().updateUiState(UI_STATE_OVERVIEW, sysuiFlags);
        }
    }

    @Override
    protected float getShiftRange() {
        return mLauncher.getDeviceProfile().widthPx / 2f;
    }

    @Override
    protected int getLogContainerTypeForNormalState() {
        return LauncherLogProto.ContainerType.NAVBAR;
    }

    @Override
    protected int getDirectionForLog() {
        return Utilities.isRtl(mLauncher.getResources()) ? Direction.LEFT : Direction.RIGHT;
    }
}
