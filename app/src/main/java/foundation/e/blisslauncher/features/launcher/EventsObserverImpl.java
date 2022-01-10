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

import android.util.Log;
import foundation.e.blisslauncher.core.events.AppAddEvent;
import foundation.e.blisslauncher.core.events.AppChangeEvent;
import foundation.e.blisslauncher.core.events.AppRemoveEvent;
import foundation.e.blisslauncher.core.events.Event;
import foundation.e.blisslauncher.core.events.EventRelay;
import foundation.e.blisslauncher.core.events.ForceReloadEvent;
import foundation.e.blisslauncher.core.events.ShortcutAddEvent;
import foundation.e.blisslauncher.core.events.TimeChangedEvent;
import java.util.Calendar;

public class EventsObserverImpl implements EventRelay.EventsObserver<Event> {

  private static final String TAG = "EventsObserverImpl";

  private LauncherActivity launcherActivity;

  public EventsObserverImpl(LauncherActivity activity) {
    this.launcherActivity = activity;
  }

  @Override
  public void accept(Event event) {
    Log.i(TAG, "accept: " + event.getEventType());
    switch (event.getEventType()) {
      case AppAddEvent.TYPE:
        launcherActivity.onAppAddEvent((AppAddEvent) event);
        break;
      case AppChangeEvent.TYPE:
        launcherActivity.onAppChangeEvent((AppChangeEvent) event);
        break;
      case AppRemoveEvent.TYPE:
        launcherActivity.onAppRemoveEvent((AppRemoveEvent) event);
        break;
      case ShortcutAddEvent.TYPE:
        launcherActivity.onShortcutAddEvent((ShortcutAddEvent) event);
        break;
      case TimeChangedEvent.TYPE:
        launcherActivity.updateAllCalendarIcons(Calendar.getInstance());
        break;
      case ForceReloadEvent.TYPE:
        launcherActivity.forceReload();
        break;
    }
  }

  @Override
  public void complete() {
    // BlissLauncher.getApplication(launcherActivity).getAppProvider().reload();
  }

  @Override
  public void clear() {
    this.launcherActivity = null;
  }
}
