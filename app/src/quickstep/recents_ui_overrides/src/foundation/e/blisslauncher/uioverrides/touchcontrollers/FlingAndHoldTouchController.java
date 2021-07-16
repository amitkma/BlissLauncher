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

import static com.android.systemui.shared.system.QuickStepContract.SYSUI_STATE_OVERVIEW_DISABLED;
import static foundation.e.blisslauncher.features.test.LauncherState.HOTSEAT_ICONS;
import static foundation.e.blisslauncher.features.test.LauncherState.NORMAL;
import static foundation.e.blisslauncher.features.test.LauncherState.OVERVIEW;
import static foundation.e.blisslauncher.features.test.LauncherState.OVERVIEW_PEEK;
import static foundation.e.blisslauncher.features.test.LauncherStateManager.ANIM_ALL;
import static foundation.e.blisslauncher.features.test.LauncherStateManager.ATOMIC_OVERVIEW_PEEK_COMPONENT;
import static foundation.e.blisslauncher.features.test.anim.AnimatorSetBuilder.ANIM_HOTSEAT_SCALE;
import static foundation.e.blisslauncher.features.test.anim.AnimatorSetBuilder.ANIM_HOTSEAT_TRANSLATE;
import static foundation.e.blisslauncher.features.test.anim.AnimatorSetBuilder.ANIM_VERTICAL_PROGRESS;
import static foundation.e.blisslauncher.features.test.anim.Interpolators.OVERSHOOT_1_2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import foundation.e.blisslauncher.features.test.LauncherState;
import foundation.e.blisslauncher.features.test.TestActivity;
import foundation.e.blisslauncher.features.test.anim.AnimatorSetBuilder;
import foundation.e.blisslauncher.quickstep.OverviewInteractionState;
import foundation.e.blisslauncher.quickstep.util.MotionPauseDetector;
import foundation.e.blisslauncher.quickstep.views.RecentsView;

/**
 * Touch controller which handles swipe and hold to go to Overview
 */
public class FlingAndHoldTouchController extends PortraitStatesTouchController {

    private static final long PEEK_IN_ANIM_DURATION = 240;
    private static final long PEEK_OUT_ANIM_DURATION = 100;
    private static final float MAX_DISPLACEMENT_PERCENT = 0.75f;

    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private final float mMotionPauseMaxDisplacement;

    private AnimatorSet mPeekAnim;

    public FlingAndHoldTouchController(TestActivity l) {
        super(l, false /* allowDragToOverview */);
        mMotionPauseDetector = new MotionPauseDetector(l);
        mMotionPauseMinDisplacement = ViewConfiguration.get(l).getScaledTouchSlop();
        mMotionPauseMaxDisplacement = getShiftRange() * MAX_DISPLACEMENT_PERCENT;
    }

    @Override
    protected long getAtomicDuration() {
        return 300;
    }

    @Override
    public void onDragStart(boolean start) {
        mMotionPauseDetector.clear();

        super.onDragStart(start);

        if (handlingOverviewAnim()) {
            mMotionPauseDetector.setOnMotionPauseListener(isPaused -> {
                RecentsView recentsView = mLauncher.getOverviewPanel();
                recentsView.setOverviewStateEnabled(isPaused);
                if (mPeekAnim != null) {
                    mPeekAnim.cancel();
                }
                LauncherState fromState = isPaused ? NORMAL : OVERVIEW_PEEK;
                LauncherState toState = isPaused ? OVERVIEW_PEEK : NORMAL;
                long peekDuration = isPaused ? PEEK_IN_ANIM_DURATION : PEEK_OUT_ANIM_DURATION;
                mPeekAnim = mLauncher.getStateManager().createAtomicAnimation(fromState, toState,
                        new AnimatorSetBuilder(), ATOMIC_OVERVIEW_PEEK_COMPONENT, peekDuration);
                mPeekAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPeekAnim = null;
                    }
                });
                mPeekAnim.start();
                recentsView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
            });
        }
    }

    /**
     * @return Whether we are handling the overview animation, rather than
     * having it as part of the existing animation to the target state.
     */
    private boolean handlingOverviewAnim() {
        int stateFlags = OverviewInteractionState.INSTANCE.get(mLauncher).getSystemUiStateFlags();
        return mStartState == NORMAL && (stateFlags & SYSUI_STATE_OVERVIEW_DISABLED) == 0;
    }

    @Override
    protected AnimatorSetBuilder getAnimatorSetBuilderForStates(LauncherState fromState,
            LauncherState toState) {
        return super.getAnimatorSetBuilderForStates(fromState, toState);
    }

    @Override
    public boolean onDrag(float displacement, MotionEvent event) {
        float upDisplacement = -displacement;
        mMotionPauseDetector.setDisallowPause(upDisplacement < mMotionPauseMinDisplacement
                || upDisplacement > mMotionPauseMaxDisplacement);
        mMotionPauseDetector.addPosition(displacement, event.getEventTime());
        return super.onDrag(displacement, event);
    }

    @Override
    public void onDragEnd(float velocity, boolean fling) {
        if (mMotionPauseDetector.isPaused() && handlingOverviewAnim()) {
            if (mPeekAnim != null) {
                mPeekAnim.cancel();
            }

            AnimatorSetBuilder builder = new AnimatorSetBuilder();
            builder.setInterpolator(ANIM_VERTICAL_PROGRESS, OVERSHOOT_1_2);
            if ((OVERVIEW.getVisibleElements(mLauncher) & HOTSEAT_ICONS) != 0) {
                builder.setInterpolator(ANIM_HOTSEAT_SCALE, OVERSHOOT_1_2);
                builder.setInterpolator(ANIM_HOTSEAT_TRANSLATE, OVERSHOOT_1_2);
            }
            AnimatorSet overviewAnim = mLauncher.getStateManager().createAtomicAnimation(
                    NORMAL, OVERVIEW, builder, ANIM_ALL, ATOMIC_DURATION);
            overviewAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onSwipeInteractionCompleted(OVERVIEW);
                }
            });
            overviewAnim.start();
        } else {
            super.onDragEnd(velocity, fling);
        }
        mMotionPauseDetector.clear();
    }

    @Override
    protected void updateAnimatorBuilderOnReinit(AnimatorSetBuilder builder) {
        if (handlingOverviewAnim()) {
            // We don't want the state transition to all apps to animate overview,
            // as that will cause a jump after our atomic animation.
            builder.addFlag(AnimatorSetBuilder.FLAG_DONT_ANIMATE_OVERVIEW);
        }
    }
}
