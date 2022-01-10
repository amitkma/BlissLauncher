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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

@TargetApi(Build.VERSION_CODES.O)
public class AddItemActivity extends AppCompatActivity {

  private static final String TAG = "AddItemActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getIntent() != null
        && getIntent().getAction().equalsIgnoreCase(LauncherApps.ACTION_CONFIRM_PIN_SHORTCUT)) {
      LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
      LauncherApps.PinItemRequest request = launcherApps.getPinItemRequest(getIntent());
      if (request == null) {
        finish();
        return;
      }

      if (request.getRequestType() == LauncherApps.PinItemRequest.REQUEST_TYPE_SHORTCUT) {
        InstallShortcutReceiver.queueShortcut(
            new ShortcutInfoCompat(request.getShortcutInfo()), this.getApplicationContext());
        request.accept();
        finish();
      }
    }
  }
}
