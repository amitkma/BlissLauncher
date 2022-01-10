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

package foundation.e.blisslauncher.features.test.anim;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import java.util.ArrayList;

/** Helper class to build a list of {@link PropertyValuesHolder} for view properties */
public class PropertyListBuilder {

  private final ArrayList<PropertyValuesHolder> mProperties = new ArrayList<>();

  public PropertyListBuilder translationX(float value) {
    mProperties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_X, value));
    return this;
  }

  public PropertyListBuilder translationY(float value) {
    mProperties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, value));
    return this;
  }

  public PropertyListBuilder scaleX(float value) {
    mProperties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, value));
    return this;
  }

  public PropertyListBuilder scaleY(float value) {
    mProperties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, value));
    return this;
  }

  /** Helper method to set both scaleX and scaleY */
  public PropertyListBuilder scale(float value) {
    return scaleX(value).scaleY(value);
  }

  public PropertyListBuilder alpha(float value) {
    mProperties.add(PropertyValuesHolder.ofFloat(View.ALPHA, value));
    return this;
  }

  public ObjectAnimator build(View view) {
    return ObjectAnimator.ofPropertyValuesHolder(
        view, mProperties.toArray(new PropertyValuesHolder[mProperties.size()]));
  }
}
