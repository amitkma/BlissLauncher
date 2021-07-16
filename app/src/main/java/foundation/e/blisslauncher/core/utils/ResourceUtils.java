/*
 * Copyright 2018 /e/.
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
package foundation.e.blisslauncher.core.utils;


import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import foundation.e.blisslauncher.core.Utilities;
import java.lang.reflect.InvocationTargetException;

public class ResourceUtils {

    public static final String NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE = "navigation_bar_width";
    public static final String NAVBAR_BOTTOM_GESTURE_SIZE = "navigation_bar_gesture_height";

    /**
     * Sets a fake configuration to the passed Resources to allow access to resources
     * accessible to a sdk level. Used to backport adaptive icon support to different
     * devices.
     *
     * @param resources the resources to set the configuration to
     * @param sdk       the sdk level to become accessible
     * @throws NoSuchMethodException     if something is wrong
     * @throws IllegalAccessException    if something is very wrong
     * @throws InvocationTargetException if something is really very extremely wrong
     */
    @SuppressLint("PrivateApi")
    public static void setFakeConfig(Resources resources, int sdk)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        int width, height;
        DisplayMetrics metrics = resources.getDisplayMetrics();
        if (metrics.widthPixels >= metrics.heightPixels) {
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        } else {
            width = metrics.heightPixels;
            height = metrics.widthPixels;
        }

        Configuration configuration = resources.getConfiguration();

        if (Utilities.ATLEAST_OREO) {
            AssetManager.class.getDeclaredMethod("setConfiguration", int.class, int.class,
                    String.class, int.class, int.class,
                    int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                    int.class, int.class,
                    int.class, int.class, int.class, int.class)
                    .invoke(resources.getAssets(), configuration.mcc, configuration.mnc,
                            configuration.locale.toLanguageTag(),
                            configuration.orientation, configuration.touchscreen,
                            configuration.densityDpi,
                            configuration.keyboard, configuration.keyboardHidden,
                            configuration.navigation,
                            width, height, configuration.smallestScreenWidthDp,
                            configuration.screenWidthDp, configuration.screenHeightDp,
                            configuration.screenLayout,
                            configuration.uiMode, configuration.colorMode, sdk);
        } else {
            AssetManager.class.getDeclaredMethod("setConfiguration", int.class, int.class,
                    String.class, int.class, int.class,
                    int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                    int.class, int.class,
                    int.class, int.class, int.class)
                    .invoke(resources.getAssets(), configuration.mcc, configuration.mnc,
                            configuration.locale.toLanguageTag(),
                            configuration.orientation, configuration.touchscreen,
                            configuration.densityDpi,
                            configuration.keyboard, configuration.keyboardHidden,
                            configuration.navigation,
                            width, height, configuration.smallestScreenWidthDp,
                            configuration.screenWidthDp, configuration.screenHeightDp,
                            configuration.screenLayout,
                            configuration.uiMode, sdk);
        }
    }

    public static int getNavbarSize(String resName, Resources res) {
        return getDimenByName(resName, res, 48);
    }

    private static int getDimenByName(String resName, Resources res, int defaultValue) {
        final int frameSize;
        final int frameSizeResID = res.getIdentifier(resName, "dimen", "android");
        if (frameSizeResID != 0) {
            frameSize = res.getDimensionPixelSize(frameSizeResID);
        } else {
            frameSize = pxFromDp(defaultValue, res.getDisplayMetrics());
        }
        return frameSize;
    }

    public static int pxFromDp(float size, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics));
    }

}
