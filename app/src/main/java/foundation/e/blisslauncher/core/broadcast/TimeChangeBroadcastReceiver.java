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
import foundation.e.blisslauncher.core.events.EventRelay;
import foundation.e.blisslauncher.core.events.TimeChangedEvent;

public class TimeChangeBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction() != null
        && (intent.getAction().equalsIgnoreCase(Intent.ACTION_TIME_CHANGED)
            || intent.getAction().equalsIgnoreCase(Intent.ACTION_DATE_CHANGED)
            || intent.getAction().equalsIgnoreCase(Intent.ACTION_TIMEZONE_CHANGED)))
      EventRelay.getInstance().push(new TimeChangedEvent());
  }

  public static TimeChangeBroadcastReceiver register(Context context) {
    IntentFilter timeIntentFilter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
    timeIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
    timeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    TimeChangeBroadcastReceiver receiver = new TimeChangeBroadcastReceiver();
    context.registerReceiver(receiver, timeIntentFilter);
    return receiver;
  }

  public static void unregister(Context context, TimeChangeBroadcastReceiver receiver) {
    context.unregisterReceiver(receiver);
  }
}
