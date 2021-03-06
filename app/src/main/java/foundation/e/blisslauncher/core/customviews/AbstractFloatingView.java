/*
 * Copyright (c) 2016 Amit Kumar.
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

package foundation.e.blisslauncher.core.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.IntDef;
import foundation.e.blisslauncher.core.Utilities;
import foundation.e.blisslauncher.features.test.BaseDragLayer;
import foundation.e.blisslauncher.features.test.BaseDraggingActivity;
import foundation.e.blisslauncher.features.test.TouchController;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Base class for a View which shows a floating UI on top of the launcher UI. */
public abstract class AbstractFloatingView extends LinearLayout implements TouchController {

  @IntDef(
      flag = true,
      value = {TYPE_FOLDER, TYPE_TASK_MENU, TYPE_OPTIONS_POPUP, TYPE_LISTENER})
  @Retention(RetentionPolicy.SOURCE)
  public @interface FloatingViewType {}

  public static final int TYPE_FOLDER = 1 << 0;
  public static final int TYPE_LISTENER = 1 << 1;

  // Popups related to quickstep UI
  public static final int TYPE_TASK_MENU = 1 << 2;
  public static final int TYPE_OPTIONS_POPUP = 1 << 3;

  public static final int TYPE_ALL =
      TYPE_FOLDER | TYPE_TASK_MENU | TYPE_OPTIONS_POPUP | TYPE_LISTENER;

  public static final int TYPE_ACCESSIBLE = TYPE_ALL & ~TYPE_LISTENER;

  public static final int TYPE_STATUS_BAR_SWIPE_DOWN_DISALLOW = TYPE_TASK_MENU;

  protected boolean mIsOpen;

  public AbstractFloatingView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AbstractFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /** We need to handle touch events to prevent them from falling through to the workspace below. */
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return true;
  }

  public final void close(boolean animate) {
    animate &= !Utilities.isPowerSaverPreventingAnimation(getContext());
    handleClose(animate);
    mIsOpen = false;
  }

  protected abstract void handleClose(boolean animate);

  public final boolean isOpen() {
    return mIsOpen;
  }

  protected void onWidgetsBound() {}

  protected abstract boolean isOfType(@FloatingViewType int type);

  /** @return Whether the back is consumed. If false, Launcher will handle the back as well. */
  public boolean onBackPressed() {
    close(true);
    return true;
  }

  @Override
  public boolean onControllerTouchEvent(MotionEvent ev) {
    return false;
  }

  protected Pair<View, String> getAccessibilityTarget() {
    return null;
  }

  protected static <T extends AbstractFloatingView> T getOpenView(
      BaseDraggingActivity activity, @FloatingViewType int type) {
    BaseDragLayer dragLayer = activity.getDragLayer();
    // Iterate in reverse order. AbstractFloatingView is added later to the dragLayer,
    // and will be one of the last views.
    for (int i = dragLayer.getChildCount() - 1; i >= 0; i--) {
      View child = dragLayer.getChildAt(i);
      if (child instanceof AbstractFloatingView) {
        AbstractFloatingView view = (AbstractFloatingView) child;
        if (view.isOfType(type) && view.isOpen()) {
          return (T) view;
        }
      }
    }
    return null;
  }

  public static void closeOpenContainer(BaseDraggingActivity activity, @FloatingViewType int type) {
    AbstractFloatingView view = getOpenView(activity, type);
    if (view != null) {
      view.close(true);
    }
  }

  public static void closeOpenViews(
      BaseDraggingActivity activity, boolean animate, @FloatingViewType int type) {
    BaseDragLayer dragLayer = activity.getDragLayer();
    // Iterate in reverse order. AbstractFloatingView is added later to the dragLayer,
    // and will be one of the last views.
    for (int i = dragLayer.getChildCount() - 1; i >= 0; i--) {
      View child = dragLayer.getChildAt(i);
      if (child instanceof AbstractFloatingView) {
        AbstractFloatingView abs = (AbstractFloatingView) child;
        if (abs.isOfType(type)) {
          abs.close(animate);
        }
      }
    }
  }

  public static void closeAllOpenViews(BaseDraggingActivity activity, boolean animate) {
    closeOpenViews(activity, animate, TYPE_ALL);
    activity.finishAutoCancelActionMode();
  }

  public static void closeAllOpenViews(BaseDraggingActivity activity) {
    closeAllOpenViews(activity, true);
  }

  public static void closeAllOpenViewsExcept(
      BaseDraggingActivity activity, boolean animate, @FloatingViewType int type) {
    closeOpenViews(activity, animate, TYPE_ALL & ~type);
    activity.finishAutoCancelActionMode();
  }

  public static void closeAllOpenViewsExcept(
      BaseDraggingActivity activity, @FloatingViewType int type) {
    closeAllOpenViewsExcept(activity, true, type);
  }

  public static AbstractFloatingView getTopOpenView(BaseDraggingActivity activity) {
    return getTopOpenViewWithType(activity, TYPE_ALL);
  }

  public static AbstractFloatingView getTopOpenViewWithType(
      BaseDraggingActivity activity, @FloatingViewType int type) {
    return getOpenView(activity, type);
  }
}
