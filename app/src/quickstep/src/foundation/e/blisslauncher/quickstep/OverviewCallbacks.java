/*
 * Copyright (c) 2018 Amit Kumar.
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

package foundation.e.blisslauncher.quickstep;

import android.content.Context;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.utils.Preconditions;
import foundation.e.blisslauncher.quickstep.util.ResourceBasedOverride;

/** Callbacks related to overview/quicksteps. */
public class OverviewCallbacks implements ResourceBasedOverride {

  private static OverviewCallbacks sInstance;

  public static OverviewCallbacks get(Context context) {
    Preconditions.assertUIThread();
    if (sInstance == null) {
      sInstance =
          Overrides.getObject(
              OverviewCallbacks.class,
              context.getApplicationContext(),
              R.string.overview_callbacks_class);
    }
    return sInstance;
  }

  public void onInitOverviewTransition() {}

  public void closeAllWindows() {}
}
