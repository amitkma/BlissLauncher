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

package foundation.e.blisslauncher.features.notification;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import foundation.e.blisslauncher.core.utils.ListUtil;

/** Created by falcon on 14/3/18. */
@TargetApi(Build.VERSION_CODES.O)
public class NotificationService extends NotificationListenerService {

  NotificationRepository mNotificationRepository;

  private static final int MSG_NOTIFICATION_POSTED = 1;
  private static final int MSG_NOTIFICATION_REMOVED = 2;
  private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;

  @Override
  public void onCreate() {
    super.onCreate();
    mNotificationRepository = NotificationRepository.getNotificationRepository();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onListenerConnected() {
    mNotificationRepository.updateNotification(ListUtil.asSafeList(getActiveNotifications()));
  }

  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {
    mNotificationRepository.updateNotification(ListUtil.asSafeList(getActiveNotifications()));
  }

  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {
    mNotificationRepository.updateNotification(ListUtil.asSafeList(getActiveNotifications()));
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }
}
