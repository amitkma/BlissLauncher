/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foundation.e.quickstep.util;

import static com.android.systemui.shared.system.QuickStepContract.supportsRoundedCornersOnWindows;

import android.content.Context;

import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.features.quickstep.util.Themes;

public class TaskCornerRadius {

    public static float get(Context context) {
        return supportsRoundedCornersOnWindows(context.getResources()) ?
                Themes.getDialogCornerRadius(context):
                context.getResources().getDimension(R.dimen.task_corner_radius_small);
    }
}
