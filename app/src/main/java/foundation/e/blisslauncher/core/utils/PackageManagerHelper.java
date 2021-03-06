/*
 * Copyright (c) 2016 Amit Kumar.
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

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.Utilities;
import foundation.e.blisslauncher.core.database.model.ApplicationItem;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.core.database.model.ShortcutItem;
import java.util.List;

/** Utility methods using package manager */
public class PackageManagerHelper {

  private static final String TAG = "PackageManagerHelper";

  private final Context mContext;
  private final PackageManager mPm;
  private final LauncherApps mLauncherApps;

  public PackageManagerHelper(Context context) {
    mContext = context;
    mPm = context.getPackageManager();
    mLauncherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
  }

  /**
   * Returns true if the app can possibly be on the SDCard. This is just a workaround and doesn't
   * guarantee that the app is on SD card.
   */
  public boolean isAppOnSdcard(String packageName, UserHandle user) {
    ApplicationInfo info = null;
    try {
      info =
          mLauncherApps.getApplicationInfo(
              packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES, user);
      return info != null
          && info.enabled
          && ((info.flags & ApplicationInfo.FLAG_INSTALLED) != 0)
          && (info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Returns whether the target app is suspended for a given user as per {@link
   * android.app.admin.DevicePolicyManager#isPackageSuspended}.
   */
  public boolean isAppSuspended(String packageName, UserHandle user) {
    ApplicationInfo info = null;
    try {
      info = mLauncherApps.getApplicationInfo(packageName, 0, user);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return info != null
        && info.enabled
        && ((info.flags & ApplicationInfo.FLAG_INSTALLED) != 0)
        && isAppSuspended(info);
  }

  public boolean isSafeMode() {
    return mContext.getPackageManager().isSafeMode();
  }

  public Intent getAppLaunchIntent(String pkg, UserHandle user) {
    List<LauncherActivityInfo> activities = mLauncherApps.getActivityList(pkg, user);
    return activities.isEmpty() ? null : ApplicationItem.makeLaunchIntent(activities.get(0));
  }

  /**
   * Returns whether an application is suspended as per {@link
   * android.app.admin.DevicePolicyManager#isPackageSuspended}.
   */
  public static boolean isAppSuspended(ApplicationInfo info) {
    // The value of FLAG_SUSPENDED was reused by a hidden constant
    // ApplicationInfo.FLAG_PRIVILEGED prior to N, so only check for suspended flag on N
    // or later.
    if (Utilities.ATLEAST_NOUGAT) {
      return (info.flags & ApplicationInfo.FLAG_SUSPENDED) != 0;
    } else {
      return false;
    }
  }

  /**
   * Returns true if {@param srcPackage} has the permission required to start the activity from
   * {@param intent}. If {@param srcPackage} is null, then the activity should not need any
   * permissions
   */
  public boolean hasPermissionForActivity(Intent intent, String srcPackage) {
    ResolveInfo target = mPm.resolveActivity(intent, 0);
    if (target == null) {
      // Not a valid target
      return false;
    }
    if (TextUtils.isEmpty(target.activityInfo.permission)) {
      // No permission is needed
      return true;
    }
    if (TextUtils.isEmpty(srcPackage)) {
      // The activity requires some permission but there is no source.
      return false;
    }

    // Source does not have sufficient permissions.
    if (mPm.checkPermission(target.activityInfo.permission, srcPackage)
        != PackageManager.PERMISSION_GRANTED) {
      return false;
    }

    if (!Utilities.ATLEAST_MARSHMALLOW) {
      // These checks are sufficient for below M devices.
      return true;
    }

    // On M and above also check AppOpsManager for compatibility mode permissions.
    if (TextUtils.isEmpty(AppOpsManager.permissionToOp(target.activityInfo.permission))) {
      // There is no app-op for this permission, which could have been disabled.
      return true;
    }

    // There is no direct way to check if the app-op is allowed for a particular app. Since
    // app-op is only enabled for apps running in compatibility mode, simply block such apps.

    try {
      return mPm.getApplicationInfo(srcPackage, 0).targetSdkVersion >= Build.VERSION_CODES.M;
    } catch (NameNotFoundException e) {
    }

    return false;
  }

  /** Starts the details activity for {@code info} */
  public void startDetailsActivityForInfo(LauncherItem info, Rect sourceBounds, Bundle opts) {
    ComponentName componentName = null;
    if (info instanceof ApplicationItem) {
      componentName = ((ApplicationItem) info).componentName;
    } else if (info instanceof ShortcutItem) {
      componentName = info.getTargetComponent();
    }
    if (componentName != null) {
      try {
        mLauncherApps.startAppDetailsActivity(
            componentName, info.user.getRealHandle(), sourceBounds, opts);
      } catch (SecurityException | ActivityNotFoundException e) {
        Toast.makeText(mContext, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Unable to launch settings", e);
      }
    }
  }

  /** Creates an intent filter to listen for actions with a specific package in the data field. */
  public static IntentFilter getPackageFilter(String pkg, String... actions) {
    IntentFilter packageFilter = new IntentFilter();
    for (String action : actions) {
      packageFilter.addAction(action);
    }
    packageFilter.addDataScheme("package");
    packageFilter.addDataSchemeSpecificPart(pkg, PatternMatcher.PATTERN_LITERAL);
    return packageFilter;
  }
}
