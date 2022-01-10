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

package foundation.e.blisslauncher.features.test.anim;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.os.Build;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Utility class to keep track of a running animation.
 *
 * <p>This class allows attaching end callbacks to an animation is intended to be used with {@link
 * AnimatorPlaybackController}, since in that case AnimationListeners are not properly dispatched.
 */
@TargetApi(Build.VERSION_CODES.O)
public class PendingAnimation {

  private final ArrayList<Consumer<OnEndListener>> mEndListeners = new ArrayList<>();

  public final AnimatorSet anim;

  public PendingAnimation(AnimatorSet anim) {
    this.anim = anim;
  }

  public void finish(boolean isSuccess) {
    for (Consumer<OnEndListener> listeners : mEndListeners) {
      listeners.accept(new OnEndListener(isSuccess));
    }
    mEndListeners.clear();
  }

  public void addEndListener(Consumer<OnEndListener> listener) {
    mEndListeners.add(listener);
  }

  public static class OnEndListener {
    public boolean isSuccess;

    public OnEndListener(boolean isSuccess) {
      this.isSuccess = isSuccess;
    }
  }
}
