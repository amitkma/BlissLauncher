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

package foundation.e.blisslauncher.features.shortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import foundation.e.blisslauncher.core.database.DatabaseManager;

public class UninstallShortcutReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent data) {
    String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
    DatabaseManager databaseManager = DatabaseManager.getManager(context);
    databaseManager.removeShortcut(name);
  }
}
