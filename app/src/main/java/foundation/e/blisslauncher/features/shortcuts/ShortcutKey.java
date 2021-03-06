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

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import foundation.e.blisslauncher.core.database.model.ShortcutItem;
import foundation.e.blisslauncher.core.utils.ComponentKey;

/** A key that uniquely identifies a shortcut using its package, id, and user handle. */
public class ShortcutKey extends ComponentKey {

  public static final String EXTRA_SHORTCUT_ID = "shortcut_id";
  private static final String INTENT_CATEGORY = "foundation.e.blisslauncher.DEEP_SHORTCUT";

  public ShortcutKey(String packageName, UserHandle user, String id) {
    // Use the id as the class name.
    super(new ComponentName(packageName, id), user);
  }

  public String getId() {
    return componentName.getClassName();
  }

  public static ShortcutKey fromInfo(ShortcutInfoCompat shortcutInfo) {
    return new ShortcutKey(
        shortcutInfo.getPackage(), shortcutInfo.getUserHandle(), shortcutInfo.getId());
  }

  public static ShortcutKey fromItem(ShortcutItem shortcutItem) {
    return new ShortcutKey(
        shortcutItem.packageName, shortcutItem.user.getRealHandle(), shortcutItem.id);
  }

  public static ShortcutKey fromIntent(Intent intent, UserHandle user) {
    String shortcutId = intent.getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID);
    return new ShortcutKey(intent.getPackage(), user, shortcutId);
  }

  public static Intent makeIntent(ShortcutInfo si) {
    return new Intent(Intent.ACTION_MAIN)
        .addCategory(INTENT_CATEGORY)
        .setComponent(si.getActivity())
        .setPackage(si.getPackage())
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        .putExtra(EXTRA_SHORTCUT_ID, si.getId());
  }
}
