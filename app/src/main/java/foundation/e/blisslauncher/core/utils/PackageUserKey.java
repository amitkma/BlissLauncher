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

package foundation.e.blisslauncher.core.utils;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.features.shortcuts.DeepShortcutManager;
import java.util.Arrays;

/** Creates a hash key based on package name and user. */
public class PackageUserKey {

  public String mPackageName;
  public UserHandle mUser;
  private int mHashCode;

  public static PackageUserKey fromItemInfo(LauncherItem info) {
    return new PackageUserKey(
        info.getTargetComponent().getPackageName(), info.user.getRealHandle());
  }

  public static PackageUserKey fromNotification(StatusBarNotification notification) {
    return new PackageUserKey(notification.getPackageName(), notification.getUser());
  }

  public PackageUserKey(String packageName, UserHandle user) {
    update(packageName, user);
  }

  private void update(String packageName, UserHandle user) {
    mPackageName = packageName;
    mUser = user;
    mHashCode = Arrays.hashCode(new Object[] {packageName, user});
  }

  /**
   * This should only be called to avoid new object creations in a loop.
   *
   * @return Whether this PackageUserKey was successfully updated - it shouldn't be used if not.
   */
  public boolean updateFromItemInfo(LauncherItem info) {
    if (DeepShortcutManager.supportsShortcuts(info)) {
      update(info.getTargetComponent().getPackageName(), info.user.getRealHandle());
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return mHashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PackageUserKey)) return false;
    PackageUserKey otherKey = (PackageUserKey) obj;
    return mPackageName.equals(otherKey.mPackageName) && mUser.equals(otherKey.mUser);
  }
}
