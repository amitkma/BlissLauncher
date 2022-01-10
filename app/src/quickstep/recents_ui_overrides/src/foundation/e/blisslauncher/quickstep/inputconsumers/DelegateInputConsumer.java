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

package foundation.e.blisslauncher.quickstep.inputconsumers;

import android.view.MotionEvent;
import com.android.systemui.shared.system.InputMonitorCompat;

public abstract class DelegateInputConsumer implements InputConsumer {

  protected static final int STATE_INACTIVE = 0;
  protected static final int STATE_ACTIVE = 1;
  protected static final int STATE_DELEGATE_ACTIVE = 2;

  protected final InputConsumer mDelegate;
  protected final InputMonitorCompat mInputMonitor;

  protected int mState;

  public DelegateInputConsumer(InputConsumer delegate, InputMonitorCompat inputMonitor) {
    mDelegate = delegate;
    mInputMonitor = inputMonitor;
    mState = STATE_INACTIVE;
  }

  @Override
  public boolean useSharedSwipeState() {
    return mDelegate.useSharedSwipeState();
  }

  @Override
  public boolean allowInterceptByParent() {
    return mDelegate.allowInterceptByParent() && mState != STATE_ACTIVE;
  }

  @Override
  public void onConsumerAboutToBeSwitched() {
    mDelegate.onConsumerAboutToBeSwitched();
  }

  protected void setActive(MotionEvent ev) {
    mState = STATE_ACTIVE;
    mInputMonitor.pilferPointers();

    // Send cancel event
    MotionEvent event = MotionEvent.obtain(ev);
    event.setAction(MotionEvent.ACTION_CANCEL);
    mDelegate.onMotionEvent(event);
    event.recycle();
  }
}
