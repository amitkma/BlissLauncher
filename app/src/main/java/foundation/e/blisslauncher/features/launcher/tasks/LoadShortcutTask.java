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

package foundation.e.blisslauncher.features.launcher.tasks;

import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import foundation.e.blisslauncher.features.launcher.AppProvider;
import foundation.e.blisslauncher.features.shortcuts.DeepShortcutManager;
import foundation.e.blisslauncher.features.shortcuts.ShortcutInfoCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadShortcutTask extends AsyncTask<Void, Void, Map<String, ShortcutInfoCompat>> {

  private AppProvider mAppProvider;

  private static final String TAG = "LoadShortcutTask";

  public LoadShortcutTask() {
    super();
  }

  public void setAppProvider(AppProvider appProvider) {
    this.mAppProvider = appProvider;
  }

  @Override
  protected Map<String, ShortcutInfoCompat> doInBackground(Void... voids) {
    List<ShortcutInfo> list =
        DeepShortcutManager.getInstance(mAppProvider.getContext())
            .queryForPinnedShortcuts(null, Process.myUserHandle());
    List<ShortcutInfoCompat> shortcutInfoCompats = new ArrayList<>(list.size());
    for (ShortcutInfo shortcutInfo : list) {
      shortcutInfoCompats.add(new ShortcutInfoCompat(shortcutInfo));
    }
    Log.i(TAG, "doInBackground: " + list.size());
    Map<String, ShortcutInfoCompat> shortcutInfoMap = new HashMap<>();
    for (ShortcutInfoCompat shortcutInfoCompat : shortcutInfoCompats) {
      shortcutInfoMap.put(shortcutInfoCompat.getId(), shortcutInfoCompat);
    }
    return shortcutInfoMap;
  }

  @Override
  protected void onPostExecute(Map<String, ShortcutInfoCompat> shortcuts) {
    super.onPostExecute(shortcuts);
    if (mAppProvider != null) {
      mAppProvider.loadShortcutsOver(shortcuts);
    }
  }
}
