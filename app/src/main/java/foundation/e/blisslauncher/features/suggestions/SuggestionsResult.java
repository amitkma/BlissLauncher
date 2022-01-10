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

package foundation.e.blisslauncher.features.suggestions;

import foundation.e.blisslauncher.core.database.model.LauncherItem;
import java.util.List;

public class SuggestionsResult {

  public static final int TYPE_LAUNCHER_ITEM = 567;
  public static final int TYPE_NETWORK_ITEM = 568;

  private List<String> networkItems;
  private List<LauncherItem> launcherItems;
  public String queryText;
  public int type = -1;

  public SuggestionsResult(String queryText) {
    this.queryText = queryText;
  }

  public List<String> getNetworkItems() {
    return networkItems;
  }

  public void setNetworkItems(List<String> networkItems) {
    this.networkItems = networkItems;
    this.type = TYPE_NETWORK_ITEM;
  }

  public List<LauncherItem> getLauncherItems() {
    return launcherItems;
  }

  public void setLauncherItems(List<LauncherItem> launcherItems) {
    this.launcherItems = launcherItems;
    this.type = TYPE_LAUNCHER_ITEM;
  }
}
