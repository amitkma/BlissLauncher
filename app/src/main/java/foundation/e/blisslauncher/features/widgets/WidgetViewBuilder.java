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

package foundation.e.blisslauncher.features.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import foundation.e.blisslauncher.BlissLauncher;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.customviews.RoundedWidgetView;
import foundation.e.blisslauncher.features.test.TestActivity;

public class WidgetViewBuilder {

  public static RoundedWidgetView create(
      TestActivity launcherActivity, @NonNull RoundedWidgetView roundedWidgetView) {
    if (BlissLauncher.getApplication(launcherActivity).getAppWidgetHost() == null) return null;
    roundedWidgetView.post(
        () ->
            updateWidgetOption(
                launcherActivity, roundedWidgetView, roundedWidgetView.getAppWidgetInfo()));

    LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    int margin = launcherActivity.getResources().getDimensionPixelSize(R.dimen.widget_margin);
    layoutParams.setMargins(0, margin, 0, margin);
    roundedWidgetView.setLayoutParams(layoutParams);

    roundedWidgetView.setOnLongClickListener(
        v -> {
          AppWidgetProviderInfo widgetProviderInfo = roundedWidgetView.getAppWidgetInfo();
          if ((widgetProviderInfo.resizeMode & AppWidgetProviderInfo.RESIZE_VERTICAL)
              == AppWidgetProviderInfo.RESIZE_VERTICAL) {
            launcherActivity.showWidgetResizeContainer(roundedWidgetView);
          } else {
            Toast.makeText(launcherActivity, "Widget is not resizable", Toast.LENGTH_SHORT).show();
          }
          return true;
        });

    return roundedWidgetView;
  }

  private static void updateWidgetOption(
      Context context, RoundedWidgetView roundedWidgetView, AppWidgetProviderInfo info) {
    Bundle newOps = new Bundle();
    newOps.putInt(
        AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
        BlissLauncher.getApplication(context).getDeviceProfile().getMaxWidgetWidth());
    newOps.putInt(
        AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
        BlissLauncher.getApplication(context).getDeviceProfile().getMaxWidgetWidth());
    newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, info.minHeight);
    newOps.putInt(
        AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
        BlissLauncher.getApplication(context).getDeviceProfile().getMaxWidgetHeight());
    roundedWidgetView.updateAppWidgetOptions(newOps);
  }
}
