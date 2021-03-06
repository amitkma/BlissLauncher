/*
 * Copyright (c) 2019 Amit Kumar.
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

package foundation.e.blisslauncher.uioverrides.touchcontrollers;

import foundation.e.blisslauncher.core.touch.SwipeDetector;
import foundation.e.blisslauncher.features.test.LauncherState;
import foundation.e.blisslauncher.features.test.TestActivity;

public class TransposedQuickSwitchTouchController extends QuickSwitchTouchController {

  public TransposedQuickSwitchTouchController(TestActivity launcher) {
    super(launcher, SwipeDetector.VERTICAL);
  }

  @Override
  protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
    return super.getTargetState(fromState, isDragTowardPositive);
  }

  @Override
  protected float initCurrentAnimation(int animComponents) {
    float multiplier = super.initCurrentAnimation(animComponents);
    return -multiplier;
  }

  @Override
  protected float getShiftRange() {
    return mLauncher.getDeviceProfile().getHeightPx() / 2f;
  }
}
