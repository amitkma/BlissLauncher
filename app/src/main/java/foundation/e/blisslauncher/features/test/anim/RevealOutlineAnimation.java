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

package foundation.e.blisslauncher.features.test.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * A {@link ViewOutlineProvider} that has helper functions to create reveal animations. This class
 * should be extended so that subclasses can define the reveal shape as the animation progresses
 * from 0 to 1.
 */
public abstract class RevealOutlineAnimation extends ViewOutlineProvider {
  protected Rect mOutline;
  protected float mOutlineRadius;

  public RevealOutlineAnimation() {
    mOutline = new Rect();
  }

  /** Returns whether elevation should be removed for the duration of the reveal animation. */
  abstract boolean shouldRemoveElevationDuringAnimation();
  /** Sets the progress, from 0 to 1, of the reveal animation. */
  abstract void setProgress(float progress);

  public ValueAnimator createRevealAnimator(final View revealView, boolean isReversed) {
    ValueAnimator va = isReversed ? ValueAnimator.ofFloat(1f, 0f) : ValueAnimator.ofFloat(0f, 1f);
    final float elevation = revealView.getElevation();

    va.addListener(
        new AnimatorListenerAdapter() {
          private boolean mIsClippedToOutline;
          private ViewOutlineProvider mOldOutlineProvider;

          public void onAnimationStart(Animator animation) {
            mIsClippedToOutline = revealView.getClipToOutline();
            mOldOutlineProvider = revealView.getOutlineProvider();

            revealView.setOutlineProvider(RevealOutlineAnimation.this);
            revealView.setClipToOutline(true);
            if (shouldRemoveElevationDuringAnimation()) {
              revealView.setTranslationZ(-elevation);
            }
          }

          public void onAnimationEnd(Animator animation) {
            revealView.setOutlineProvider(mOldOutlineProvider);
            revealView.setClipToOutline(mIsClippedToOutline);
            if (shouldRemoveElevationDuringAnimation()) {
              revealView.setTranslationZ(0);
            }
          }
        });

    va.addUpdateListener(
        valueAnimator -> {
          float progress = (Float) valueAnimator.getAnimatedValue();
          setProgress(progress);
          revealView.invalidateOutline();
        });
    return va;
  }

  @Override
  public void getOutline(View v, Outline outline) {
    outline.setRoundRect(mOutline, mOutlineRadius);
  }

  public float getRadius() {
    return mOutlineRadius;
  }

  public void getOutline(Rect out) {
    out.set(mOutline);
  }
}
