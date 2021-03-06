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

package foundation.e.blisslauncher.quickstep.views;

import static foundation.e.blisslauncher.quickstep.views.TaskThumbnailView.DIM_ALPHA;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.customviews.AbstractFloatingView;
import foundation.e.blisslauncher.features.test.BaseDragLayer;
import foundation.e.blisslauncher.features.test.BaseDraggingActivity;
import foundation.e.blisslauncher.features.test.anim.AnimationSuccessListener;
import foundation.e.blisslauncher.features.test.anim.Interpolators;
import foundation.e.blisslauncher.features.test.anim.RoundedRectRevealOutlineProvider;
import foundation.e.blisslauncher.quickstep.TaskOverlayFactory;
import foundation.e.blisslauncher.quickstep.TaskSystemShortcut;
import foundation.e.blisslauncher.quickstep.TaskUtils;
import foundation.e.blisslauncher.quickstep.util.Themes;
import java.util.List;

/** Contains options for a recent task when long-pressing its icon. */
public class TaskMenuView extends AbstractFloatingView {

  private static final Rect sTempRect = new Rect();

  private static final int REVEAL_OPEN_DURATION = 150;
  private static final int REVEAL_CLOSE_DURATION = 100;

  private final float mThumbnailTopMargin;
  private BaseDraggingActivity mActivity;
  private TextView mTaskName;
  private IconView mTaskIcon;
  private AnimatorSet mOpenCloseAnimator;
  private TaskView mTaskView;
  private LinearLayout mOptionLayout;

  public TaskMenuView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TaskMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mActivity = BaseDraggingActivity.fromContext(context);
    mThumbnailTopMargin = getResources().getDimension(R.dimen.task_thumbnail_top_margin);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mTaskName = findViewById(R.id.task_name);
    mTaskIcon = findViewById(R.id.task_icon);
    mOptionLayout = findViewById(R.id.menu_option_layout);
  }

  @Override
  public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      BaseDragLayer dl = mActivity.getDragLayer();
      if (!dl.isEventOverView(this, ev)) {
        // TODO: log this once we have a new container type for it?
        close(true);
        return true;
      }
    }
    return false;
  }

  @Override
  protected void handleClose(boolean animate) {
    if (animate) {
      animateClose();
    } else {
      closeComplete();
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  @Override
  protected boolean isOfType(int type) {
    return (type & TYPE_TASK_MENU) != 0;
  }

  public void setPosition(float x, float y) {
    setX(x);
    setY(y + mThumbnailTopMargin);
  }

  public static TaskMenuView showForTask(TaskView taskView) {
    BaseDraggingActivity activity = BaseDraggingActivity.fromContext(taskView.getContext());
    final TaskMenuView taskMenuView =
        (TaskMenuView)
            activity
                .getLayoutInflater()
                .inflate(R.layout.task_menu, activity.getDragLayer(), false);
    return taskMenuView.populateAndShowForTask(taskView) ? taskMenuView : null;
  }

  private boolean populateAndShowForTask(TaskView taskView) {
    if (isAttachedToWindow()) {
      return false;
    }
    mActivity.getDragLayer().addView(this);
    mTaskView = taskView;
    addMenuOptions(mTaskView);
    orientAroundTaskView(mTaskView);
    post(this::animateOpen);
    return true;
  }

  private void addMenuOptions(TaskView taskView) {
    Drawable icon = taskView.getTask().icon.getConstantState().newDrawable();
    mTaskIcon.setDrawable(icon);
    mTaskIcon.setOnClickListener(v -> close(true));
    mTaskName.setText(TaskUtils.getTitle(getContext(), taskView.getTask()));
    mTaskName.setOnClickListener(v -> close(true));

    // Move the icon and text up half an icon size to lay over the TaskView
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTaskIcon.getLayoutParams();
    params.topMargin = (int) -mThumbnailTopMargin;
    mTaskIcon.setLayoutParams(params);

    final BaseDraggingActivity activity = BaseDraggingActivity.fromContext(getContext());
    final List<TaskSystemShortcut> shortcuts =
        TaskOverlayFactory.INSTANCE.get(getContext()).getEnabledShortcuts(taskView);
    final int count = shortcuts.size();
    for (int i = 0; i < count; ++i) {
      final TaskSystemShortcut menuOption = shortcuts.get(i);
      addMenuOption(menuOption, menuOption.getOnClickListener(activity, taskView));
    }
  }

  private void addMenuOption(TaskSystemShortcut menuOption, OnClickListener onClickListener) {
    ViewGroup menuOptionView =
        (ViewGroup)
            mActivity.getLayoutInflater().inflate(R.layout.task_view_menu_option, this, false);
    menuOption.setIconAndLabelFor(
        menuOptionView.findViewById(R.id.icon), menuOptionView.findViewById(R.id.text));
    menuOptionView.setOnClickListener(onClickListener);
    mOptionLayout.addView(menuOptionView);
  }

  private void orientAroundTaskView(TaskView taskView) {
    measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    mActivity.getDragLayer().getDescendantRectRelativeToSelf(taskView, sTempRect);
    Rect insets = mActivity.getDragLayer().mInsets;
    BaseDragLayer.LayoutParams params = (BaseDragLayer.LayoutParams) getLayoutParams();
    params.width = taskView.getMeasuredWidth();
    params.gravity = Gravity.START;
    setLayoutParams(params);
    setScaleX(taskView.getScaleX());
    setScaleY(taskView.getScaleY());
    setPosition(sTempRect.left - insets.left, sTempRect.top - insets.top);
  }

  private void animateOpen() {
    animateOpenOrClosed(false);
    mIsOpen = true;
  }

  private void animateClose() {
    animateOpenOrClosed(true);
  }

  private void animateOpenOrClosed(boolean closing) {
    if (mOpenCloseAnimator != null && mOpenCloseAnimator.isRunning()) {
      mOpenCloseAnimator.end();
    }
    mOpenCloseAnimator = new AnimatorSet();

    final Animator revealAnimator =
        createOpenCloseOutlineProvider().createRevealAnimator(this, closing);
    revealAnimator.setInterpolator(Interpolators.DEACCEL);
    mOpenCloseAnimator.play(revealAnimator);
    mOpenCloseAnimator.play(
        ObjectAnimator.ofFloat(
            mTaskView.getThumbnail(), DIM_ALPHA, closing ? 0 : TaskView.MAX_PAGE_SCRIM_ALPHA));
    mOpenCloseAnimator.addListener(
        new AnimationSuccessListener() {
          @Override
          public void onAnimationStart(Animator animation) {
            setVisibility(VISIBLE);
          }

          @Override
          public void onAnimationSuccess(Animator animator) {
            if (closing) {
              closeComplete();
            }
          }
        });
    mOpenCloseAnimator.play(ObjectAnimator.ofFloat(this, ALPHA, closing ? 0 : 1));
    mOpenCloseAnimator.setDuration(closing ? REVEAL_CLOSE_DURATION : REVEAL_OPEN_DURATION);
    mOpenCloseAnimator.start();
  }

  private void closeComplete() {
    mIsOpen = false;
    mActivity.getDragLayer().removeView(this);
  }

  private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
    float radius = Themes.getDialogCornerRadius(getContext());
    Rect fromRect = new Rect(0, 0, getWidth(), 0);
    Rect toRect = new Rect(0, 0, getWidth(), getHeight());
    return new RoundedRectRevealOutlineProvider(radius, radius, fromRect, toRect);
  }

  public View findMenuItemByText(String text) {
    for (int i = mOptionLayout.getChildCount() - 1; i >= 0; --i) {
      final ViewGroup menuOptionView = (ViewGroup) mOptionLayout.getChildAt(i);
      if (text.equals(menuOptionView.<TextView>findViewById(R.id.text).getText())) {
        return menuOptionView;
      }
    }
    return null;
  }
}
