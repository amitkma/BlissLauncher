/*
 * Copyright 2018 Amit Kumar.
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
package foundation.e.blisslauncher;

import static androidx.dynamicanimation.animation.DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS;
import static foundation.e.blisslauncher.features.test.LauncherState.NORMAL;
import static foundation.e.blisslauncher.features.test.anim.Interpolators.AGGRESSIVE_EASE;
import static foundation.e.blisslauncher.features.test.anim.Interpolators.LINEAR;
import static foundation.e.blisslauncher.features.test.anim.LauncherAnimUtils.VIEW_TRANSLATE_X;
import static foundation.e.blisslauncher.quickstep.TaskViewUtils.findTaskViewToLaunch;
import static foundation.e.blisslauncher.quickstep.TaskViewUtils.getRecentsWindowAnimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import foundation.e.blisslauncher.features.test.anim.AnimatorPlaybackController;
import foundation.e.blisslauncher.features.test.anim.Interpolators;
import foundation.e.blisslauncher.features.test.anim.SpringObjectAnimator;
import foundation.e.blisslauncher.quickstep.util.ClipAnimationHelper;
import foundation.e.blisslauncher.quickstep.views.RecentsView;
import foundation.e.blisslauncher.quickstep.views.TaskView;

/**
 * A {@link QuickstepAppTransitionManagerImpl} that also implements recents transitions from
 * {@link RecentsView}.
 */
public final class LauncherAppTransitionManagerImpl extends QuickstepAppTransitionManagerImpl {

    public static final int INDEX_SHELF_ANIM = 0;
    public static final int INDEX_RECENTS_FADE_ANIM = 1;
    public static final int INDEX_RECENTS_TRANSLATE_X_ANIM = 2;

    public LauncherAppTransitionManagerImpl(Context context) {
        super(context);
    }

    @Override
    protected boolean isLaunchingFromRecents(@NonNull View v,
            @Nullable RemoteAnimationTargetCompat[] targets) {
        return mLauncher.getStateManager().getState().overviewUi
                && findTaskViewToLaunch(mLauncher, v, targets) != null;
    }

    @Override
    protected void composeRecentsLaunchAnimator(@NonNull AnimatorSet anim, @NonNull View v,
            @NonNull RemoteAnimationTargetCompat[] targets, boolean launcherClosing) {
        RecentsView recentsView = mLauncher.getOverviewPanel();
        boolean skipLauncherChanges = !launcherClosing;

        TaskView taskView = findTaskViewToLaunch(mLauncher, v, targets);

        ClipAnimationHelper helper = new ClipAnimationHelper(mLauncher);
        anim.play(getRecentsWindowAnimator(taskView, skipLauncherChanges, targets, helper)
                .setDuration(RECENTS_LAUNCH_DURATION));

        Animator childStateAnimation = null;
        // Found a visible recents task that matches the opening app, lets launch the app from there
        Animator launcherAnim;
        final AnimatorListenerAdapter windowAnimEndListener;
        if (launcherClosing) {
            launcherAnim = recentsView.createAdjacentPageAnimForTaskLaunch(taskView, helper);
            launcherAnim.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            launcherAnim.setDuration(RECENTS_LAUNCH_DURATION);

            // Make sure recents gets fixed up by resetting task alphas and scales, etc.
            windowAnimEndListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLauncher.getStateManager().moveToRestState();
                    mLauncher.getStateManager().reapplyState();
                }
            };
        } else {
            AnimatorPlaybackController controller =
                    mLauncher.getStateManager().createAnimationToNewWorkspace(NORMAL,
                            RECENTS_LAUNCH_DURATION);
            controller.dispatchOnStart();
            childStateAnimation = controller.getTarget();
            launcherAnim = controller.getAnimationPlayer().setDuration(RECENTS_LAUNCH_DURATION);
            windowAnimEndListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLauncher.getStateManager().goToState(NORMAL, false);
                }
            };
        }
        anim.play(launcherAnim);

        // Set the current animation first, before adding windowAnimEndListener. Setting current
        // animation adds some listeners which need to be called before windowAnimEndListener
        // (the ordering of listeners matter in this case).
        mLauncher.getStateManager().setCurrentAnimation(anim, childStateAnimation);
        anim.addListener(windowAnimEndListener);
    }

    @Override
    protected Runnable composeViewContentAnimator(@NonNull AnimatorSet anim, float[] alphas,
            float[] trans) {
        RecentsView overview = mLauncher.getOverviewPanel();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(overview,
                RecentsView.CONTENT_ALPHA, alphas);
        alpha.setDuration(CONTENT_ALPHA_DURATION);
        alpha.setInterpolator(LINEAR);
        anim.play(alpha);
        overview.setFreezeViewVisibility(true);

        ObjectAnimator transY = ObjectAnimator.ofFloat(overview, View.TRANSLATION_Y, trans);
        transY.setInterpolator(AGGRESSIVE_EASE);
        transY.setDuration(CONTENT_TRANSLATION_DURATION);
        anim.play(transY);

        return () -> {
            overview.setFreezeViewVisibility(false);
            mLauncher.getStateManager().reapplyState();
        };
    }

    @Override
    public int getStateElementAnimationsCount() {
        return 3;
    }

    @Override
    public Animator createStateElementAnimation(int index, float... values) {
        switch (index) {
            case INDEX_RECENTS_FADE_ANIM:
                return ObjectAnimator.ofFloat(mLauncher.getOverviewPanel(),
                        RecentsView.CONTENT_ALPHA, values);
            case INDEX_RECENTS_TRANSLATE_X_ANIM:
                return new SpringObjectAnimator<>(mLauncher.getOverviewPanel(),
                        VIEW_TRANSLATE_X, MIN_VISIBLE_CHANGE_PIXELS, 0.8f, 250, values);
            default:
                return super.createStateElementAnimation(index, values);
        }
    }
}
