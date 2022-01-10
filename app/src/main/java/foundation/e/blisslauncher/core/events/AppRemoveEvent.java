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

package foundation.e.blisslauncher.core.events;

import foundation.e.blisslauncher.core.utils.UserHandle;

public class AppRemoveEvent extends Event {
  private String packageName;
  private UserHandle userHandle;

  public static final int TYPE = 602;

  public AppRemoveEvent(String packageName, UserHandle userHandle) {
    super(TYPE);
    this.packageName = packageName;
    this.userHandle = userHandle;
  }

  public String getPackageName() {
    return packageName;
  }

  public UserHandle getUserHandle() {
    return userHandle;
  }
}
