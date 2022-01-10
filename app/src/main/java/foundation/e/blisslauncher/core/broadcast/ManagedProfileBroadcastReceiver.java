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

package foundation.e.blisslauncher.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import foundation.e.blisslauncher.core.events.EventRelay;
import foundation.e.blisslauncher.core.events.ForceReloadEvent;

public class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i("PROFILE", "onReceive: " + intent.getAction());
    final String action = intent.getAction();
    if (Intent.ACTION_MANAGED_PROFILE_ADDED.equals(action)
        || Intent.ACTION_MANAGED_PROFILE_REMOVED.equals(action)) {
      EventRelay.getInstance().push(new ForceReloadEvent());
    } else if (Intent.ACTION_MANAGED_PROFILE_AVAILABLE.equals(action)
        || Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action)
        || Intent.ACTION_MANAGED_PROFILE_UNLOCKED.equals(action)) {
      UserHandle user = intent.getParcelableExtra(Intent.EXTRA_USER);
      if (user != null) {
        /*if (Intent.ACTION_MANAGED_PROFILE_AVAILABLE.equals(action) ||
                Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action)) {
            //enqueueModelUpdateTask(new PackageUpdatedTask(PackageUpdatedTask.OP_USER_AVAILABILITY_CHANGE, user));
        }

        // ACTION_MANAGED_PROFILE_UNAVAILABLE sends the profile back to locked mode, so
        // we need to run the state change task again.
        if (Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action) ||
                Intent.ACTION_MANAGED_PROFILE_UNLOCKED.equals(action)) {
            //enqueueModelUpdateTask(new UserLockStateChangedTask(user));
        }*/

        // TODO: Need to handle it more gracefully. Currently it just recreate the launcher.
        EventRelay.getInstance().push(new ForceReloadEvent());
      }
    }
  }

  public static ManagedProfileBroadcastReceiver register(Context context) {
    IntentFilter timeIntentFilter = new IntentFilter(Intent.ACTION_MANAGED_PROFILE_ADDED);
    timeIntentFilter.addAction(Intent.ACTION_MANAGED_PROFILE_REMOVED);
    timeIntentFilter.addAction(Intent.ACTION_MANAGED_PROFILE_AVAILABLE);
    timeIntentFilter.addAction(Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE);
    timeIntentFilter.addAction(Intent.ACTION_MANAGED_PROFILE_UNLOCKED);
    ManagedProfileBroadcastReceiver receiver = new ManagedProfileBroadcastReceiver();
    context.registerReceiver(receiver, timeIntentFilter);
    return receiver;
  }

  public static void unregister(Context context, ManagedProfileBroadcastReceiver receiver) {
    context.unregisterReceiver(receiver);
  }
}
