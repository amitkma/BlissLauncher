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

package foundation.e.blisslauncher.features.launcher;

import com.jakewharton.rxrelay2.BehaviorRelay;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import java.util.List;

public class AppsRepository {

  private static final String TAG = "AppsRepository";
  private BehaviorRelay<List<LauncherItem>> appsRelay;

  private static AppsRepository sAppsRepository;

  private AppsRepository() {
    appsRelay = BehaviorRelay.create();
  }

  public static AppsRepository getAppsRepository() {
    if (sAppsRepository == null) {
      sAppsRepository = new AppsRepository();
    }
    return sAppsRepository;
  }

  public void clearAll() {
    appsRelay = BehaviorRelay.create();
  }

  public void updateAppsRelay(List<LauncherItem> launcherItems) {
    this.appsRelay.accept(launcherItems);
  }

  public BehaviorRelay<List<LauncherItem>> getAppsRelay() {
    return appsRelay;
  }
}
