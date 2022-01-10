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

package foundation.e.blisslauncher.core.executors;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
  private static final AppExecutors ourInstance = new AppExecutors();
  private ExecutorService diskExecutor;
  private ExecutorService appExecutor;
  private Executor searchExecutor;
  private ExecutorService shortcutExecutor;

  public static AppExecutors getInstance() {
    return ourInstance;
  }

  private AppExecutors() {
    diskExecutor = Executors.newSingleThreadExecutor();
    appExecutor = Executors.newSingleThreadExecutor();
    shortcutExecutor = Executors.newSingleThreadExecutor();
  }

  public ExecutorService diskIO() {
    return diskExecutor;
  }

  public ExecutorService appIO() {
    return appExecutor;
  }

  public ExecutorService shortcutIO() {
    return shortcutExecutor;
  }
}
