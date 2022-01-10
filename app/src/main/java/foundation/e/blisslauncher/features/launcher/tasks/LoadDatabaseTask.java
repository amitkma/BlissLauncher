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

import android.os.AsyncTask;
import foundation.e.blisslauncher.core.database.LauncherDB;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.core.migrate.Migration;
import foundation.e.blisslauncher.features.launcher.AppProvider;
import java.util.List;

public class LoadDatabaseTask extends AsyncTask<Void, Void, List<LauncherItem>> {

  private AppProvider mAppProvider;

  public LoadDatabaseTask() {
    super();
  }

  public void setAppProvider(AppProvider appProvider) {
    this.mAppProvider = appProvider;
  }

  @Override
  protected List<LauncherItem> doInBackground(Void... voids) {
    Migration.migrateSafely(mAppProvider.getContext());
    return LauncherDB.getDatabase(mAppProvider.getContext()).launcherDao().getAllItems();
  }

  @Override
  protected void onPostExecute(List<LauncherItem> launcherItems) {
    super.onPostExecute(launcherItems);
    if (mAppProvider != null) {
      mAppProvider.loadDatabaseOver(launcherItems);
    }
  }
}
