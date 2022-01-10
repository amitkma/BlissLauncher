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

import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

  // Minimal x and y axis swipe distance.
  private static int MIN_SWIPE_DISTANCE_X = 100;
  private static int MIN_SWIPE_DISTANCE_Y = 100;

  // Maximal x and y axis swipe distance.
  private static int MAX_SWIPE_DISTANCE_X = 1000;
  private static int MAX_SWIPE_DISTANCE_Y = 1000;

  // Source activity that display message in text view.
  private OnSwipeDownListener mOnSwipeDownListener = null;

  public void setListener(OnSwipeDownListener listener) {
    mOnSwipeDownListener = listener;
  }

  @Override
  public boolean onDown(MotionEvent e) {
    return true;
  }

  /* This method is invoked when a swipe gesture happened. */
  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

    // Get swipe delta value in x axis.
    float deltaX = e1.getX() - e2.getX();

    // Get swipe delta value in y axis.
    float deltaY = e1.getY() - e2.getY();

    // Get absolute value.
    float deltaXAbs = Math.abs(deltaX);
    float deltaYAbs = Math.abs(deltaY);

    if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
      if (deltaY < 0) {
        this.mOnSwipeDownListener.onSwipeFinish();
        return true;
      }
    }
    return false;
  }

  // Invoked when single tap screen.
  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    return false;
  }

  // Invoked when double tap screen.
  @Override
  public boolean onDoubleTap(MotionEvent e) {
    return false;
  }
}
