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

package foundation.e.blisslauncher.quickstep;

import static android.view.Display.DEFAULT_DISPLAY;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecCompat;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecsFuture;
import com.android.systemui.shared.recents.view.RecentsTransition;
import com.android.systemui.shared.system.ActivityCompat;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.WindowManagerWrapper;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.customviews.AbstractFloatingView;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.core.database.model.ShortcutItem;
import foundation.e.blisslauncher.core.utils.PackageManagerHelper;
import foundation.e.blisslauncher.features.test.BaseDraggingActivity;
import foundation.e.blisslauncher.features.test.VariantDeviceProfile;
import foundation.e.blisslauncher.quickstep.views.RecentsView;
import foundation.e.blisslauncher.quickstep.views.TaskThumbnailView;
import foundation.e.blisslauncher.quickstep.views.TaskView;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** Represents a system shortcut that can be shown for a recent task. */
public class TaskSystemShortcut {

  private static final String TAG = "TaskSystemShortcut";

  public final int iconResId;
  public final int labelResId;

  private final CharSequence mLabel;
  private final CharSequence mContentDescription;
  private final Drawable mIcon;

  public TaskSystemShortcut(int iconResId, int labelResId) {
    this.iconResId = iconResId;
    this.labelResId = labelResId;
    mIcon = null;
    mLabel = null;
    mContentDescription = null;
  }

  public View.OnClickListener getOnClickListener(
      BaseDraggingActivity activity, LauncherItem itemInfo) {
    return null;
  }

  public View.OnClickListener getOnClickListener(BaseDraggingActivity activity, TaskView view) {
    Task task = view.getTask();

    ShortcutItem dummyInfo = new ShortcutItem();
    dummyInfo.launchIntent = new Intent();
    ComponentName component = task.getTopComponent();
    dummyInfo.launchIntent.setComponent(component);
    dummyInfo.user =
        new foundation.e.blisslauncher.core.utils.UserHandle(
            task.key.userId, android.os.UserHandle.of(task.key.userId));
    dummyInfo.title = TaskUtils.getTitle(activity, task);

    return getOnClickListenerForTask(activity, dummyInfo);
  }

  protected View.OnClickListener getOnClickListenerForTask(
      BaseDraggingActivity activity, LauncherItem dummyInfo) {
    return getOnClickListener(activity, dummyInfo);
  }

  public void setIconAndLabelFor(View iconView, TextView labelView) {
    if (mIcon != null) {
      iconView.setBackground(mIcon);
    } else {
      iconView.setBackgroundResource(iconResId);
    }

    if (mLabel != null) {
      labelView.setText(mLabel);
    } else {
      labelView.setText(labelResId);
    }
  }

  public static class AppInfo extends TaskSystemShortcut {
    public AppInfo() {
      super(R.drawable.ic_info_no_shadow, R.string.app_info_drop_target_label);
    }

    @Override
    public View.OnClickListener getOnClickListener(
        BaseDraggingActivity activity, LauncherItem itemInfo) {
      return (view) -> {
        Rect sourceBounds = activity.getViewBounds(view);
        Bundle opts = activity.getActivityLaunchOptionsAsBundle(view);
        new PackageManagerHelper(activity)
            .startDetailsActivityForInfo(itemInfo, sourceBounds, opts);
      };
    }
  }

  public abstract static class MultiWindow extends TaskSystemShortcut {

    private Handler mHandler;

    public MultiWindow(int iconRes, int textRes) {
      super(iconRes, textRes);
      mHandler = new Handler(Looper.getMainLooper());
    }

    protected abstract boolean isAvailable(BaseDraggingActivity activity, int displayId);

    protected abstract ActivityOptions makeLaunchOptions(Activity activity);

    protected abstract boolean onActivityStarted(BaseDraggingActivity activity);

    @Override
    public View.OnClickListener getOnClickListener(
        BaseDraggingActivity activity, TaskView taskView) {
      final Task task = taskView.getTask();
      final int taskId = task.key.id;
      final int displayId = task.key.displayId;
      if (!task.isDockable) {
        return null;
      }
      if (!isAvailable(activity, displayId)) {
        return null;
      }
      final RecentsView recentsView = activity.getOverviewPanel();

      final TaskThumbnailView thumbnailView = taskView.getThumbnail();
      return (v -> {
        final View.OnLayoutChangeListener onLayoutChangeListener =
            new View.OnLayoutChangeListener() {
              @Override
              public void onLayoutChange(
                  View v, int l, int t, int r, int b, int oldL, int oldT, int oldR, int oldB) {
                taskView.getRootView().removeOnLayoutChangeListener(this);
                recentsView.clearIgnoreResetTask(taskId);

                // Start animating in the side pages once launcher has been resized
                recentsView.dismissTask(taskView, false, false);
              }
            };

        final VariantDeviceProfile.OnDeviceProfileChangeListener onDeviceProfileChangeListener =
            new VariantDeviceProfile.OnDeviceProfileChangeListener() {
              @Override
              public void onDeviceProfileChanged(VariantDeviceProfile dp) {
                activity.removeOnDeviceProfileChangeListener(this);
                if (dp.isMultiWindowMode) {
                  taskView.getRootView().addOnLayoutChangeListener(onLayoutChangeListener);
                }
              }
            };

        dismissTaskMenuView(activity);

        ActivityOptions options = makeLaunchOptions(activity);
        if (options != null
            && ActivityManagerWrapper.getInstance().startActivityFromRecents(taskId, options)) {
          if (!onActivityStarted(activity)) {
            return;
          }
          // Add a device profile change listener to kick off animating the side tasks
          // once we enter multiwindow mode and relayout
          activity.addOnDeviceProfileChangeListener(onDeviceProfileChangeListener);

          final Runnable animStartedListener =
              () -> {
                // Hide the task view and wait for the window to be resized
                // TODO: Consider animating in launcher and do an in-place start activity
                //       afterwards
                recentsView.setIgnoreResetTask(taskId);
                taskView.setAlpha(0f);
              };

          final int[] position = new int[2];
          thumbnailView.getLocationOnScreen(position);
          final int width = (int) (thumbnailView.getWidth() * taskView.getScaleX());
          final int height = (int) (thumbnailView.getHeight() * taskView.getScaleY());
          final Rect taskBounds =
              new Rect(position[0], position[1], position[0] + width, position[1] + height);

          // Take the thumbnail of the task without a scrim and apply it back after
          float alpha = thumbnailView.getDimAlpha();
          thumbnailView.setDimAlpha(0);
          Bitmap thumbnail =
              RecentsTransition.drawViewIntoHardwareBitmap(
                  taskBounds.width(), taskBounds.height(), thumbnailView, 1f, Color.BLACK);
          thumbnailView.setDimAlpha(alpha);

          AppTransitionAnimationSpecsFuture future =
              new AppTransitionAnimationSpecsFuture(mHandler) {
                @Override
                public List<AppTransitionAnimationSpecCompat> composeSpecs() {
                  return Collections.singletonList(
                      new AppTransitionAnimationSpecCompat(taskId, thumbnail, taskBounds));
                }
              };
          WindowManagerWrapper.getInstance()
              .overridePendingAppTransitionMultiThumbFuture(
                  future,
                  animStartedListener,
                  mHandler,
                  true /* scaleUp */,
                  v.getDisplay().getDisplayId());
        }
      });
    }
  }

  public static class SplitScreen extends MultiWindow {
    public SplitScreen() {
      super(R.drawable.ic_split_screen, R.string.recent_task_option_split_screen);
    }

    @Override
    protected boolean isAvailable(BaseDraggingActivity activity, int displayId) {
      // Don't show menu-item if already in multi-window and the task is from
      // the secondary display.
      // TODO(b/118266305): Temporarily disable splitscreen for secondary display while new
      // implementation is enabled
      return !activity.getDeviceProfile().isMultiWindowMode
          && (displayId == -1 || displayId == DEFAULT_DISPLAY);
    }

    @Override
    protected ActivityOptions makeLaunchOptions(Activity activity) {
      final ActivityCompat act = new ActivityCompat(activity);
      final int navBarPosition =
          WindowManagerWrapper.getInstance().getNavBarPosition(act.getDisplayId());
      if (navBarPosition == WindowManagerWrapper.NAV_BAR_POS_INVALID) {
        return null;
      }
      boolean dockTopOrLeft = navBarPosition != WindowManagerWrapper.NAV_BAR_POS_LEFT;
      return ActivityOptionsCompat.makeSplitScreenOptions(dockTopOrLeft);
    }

    @Override
    protected boolean onActivityStarted(BaseDraggingActivity activity) {
      ISystemUiProxy sysUiProxy = RecentsModel.INSTANCE.get(activity).getSystemUiProxy();
      try {
        sysUiProxy.onSplitScreenInvoked();
      } catch (RemoteException e) {
        Log.w(TAG, "Failed to notify SysUI of split screen: ", e);
        return false;
      }
      return true;
    }
  }

  public static class Pin extends TaskSystemShortcut {

    private static final String TAG = Pin.class.getSimpleName();

    private Handler mHandler;

    public Pin() {
      super(R.drawable.ic_pin, R.string.recent_task_option_pin);
      mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View.OnClickListener getOnClickListener(
        BaseDraggingActivity activity, TaskView taskView) {
      ISystemUiProxy sysUiProxy = RecentsModel.INSTANCE.get(activity).getSystemUiProxy();
      if (sysUiProxy == null) {
        return null;
      }
      if (!ActivityManagerWrapper.getInstance().isScreenPinningEnabled()) {
        return null;
      }
      if (ActivityManagerWrapper.getInstance().isLockToAppActive()) {
        // We shouldn't be able to pin while an app is locked.
        return null;
      }
      return view -> {
        Consumer<Boolean> resultCallback =
            success -> {
              if (success) {
                try {
                  sysUiProxy.startScreenPinning(taskView.getTask().key.id);
                } catch (RemoteException e) {
                  Log.w(TAG, "Failed to start screen pinning: ", e);
                }
              } else {
                taskView.notifyTaskLaunchFailed(TAG);
              }
            };
        taskView.launchTask(true, resultCallback, mHandler);
        dismissTaskMenuView(activity);
      };
    }
  }

  protected static void dismissTaskMenuView(BaseDraggingActivity activity) {
    AbstractFloatingView.closeOpenViews(activity, true, AbstractFloatingView.TYPE_ALL);
  }
}
