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

import android.graphics.Matrix;
import android.view.View;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.features.test.BaseActivity;
import foundation.e.blisslauncher.features.test.BaseDraggingActivity;
import foundation.e.blisslauncher.quickstep.util.ResourceBasedOverride;
import foundation.e.blisslauncher.quickstep.views.TaskThumbnailView;
import foundation.e.blisslauncher.quickstep.views.TaskView;
import java.util.ArrayList;
import java.util.List;

/** Factory class to create and add an overlays on the TaskView */
public class TaskOverlayFactory implements ResourceBasedOverride {

  /** Note that these will be shown in order from top to bottom, if available for the task. */
  private static final TaskSystemShortcut[] MENU_OPTIONS =
      new TaskSystemShortcut[] {
        new TaskSystemShortcut.AppInfo(),
        new TaskSystemShortcut.SplitScreen(),
        new TaskSystemShortcut.Pin(),
      };

  public static final MainThreadInitializedObject<TaskOverlayFactory> INSTANCE =
      new MainThreadInitializedObject<>(
          c ->
              Overrides.getObject(
                  TaskOverlayFactory.class, c, R.string.task_overlay_factory_class));

  public List<TaskSystemShortcut> getEnabledShortcuts(TaskView taskView) {
    final ArrayList<TaskSystemShortcut> shortcuts = new ArrayList<>();
    final BaseDraggingActivity activity =
        (BaseDraggingActivity) BaseActivity.fromContext(taskView.getContext());
    for (TaskSystemShortcut menuOption : MENU_OPTIONS) {
      View.OnClickListener onClickListener = menuOption.getOnClickListener(activity, taskView);
      if (onClickListener != null) {
        shortcuts.add(menuOption);
      }
    }
    return shortcuts;
  }

  public TaskOverlay createOverlay(TaskThumbnailView thumbnailView) {
    return new TaskOverlay();
  }

  public static class TaskOverlay {

    /** Called when the current task is interactive for the user */
    public void initOverlay(Task task, ThumbnailData thumbnail, Matrix matrix) {}

    /** Called when the overlay is no longer used. */
    public void reset() {}
  }
}
