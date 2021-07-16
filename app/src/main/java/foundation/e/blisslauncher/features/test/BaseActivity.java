/* Copyright (C) 2017 The Android Open Source Project
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


package foundation.e.blisslauncher.features.test;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View.AccessibilityDelegate;
import androidx.annotation.IntDef;
import foundation.e.blisslauncher.core.utils.ViewCache;
import java.lang.annotation.Retention;
import java.util.ArrayList;

public abstract class BaseActivity extends Activity {

    public static final int INVISIBLE_BY_STATE_HANDLER = 1 << 0;
    public static final int INVISIBLE_BY_APP_TRANSITIONS = 1 << 1;
    public static final int INVISIBLE_BY_PENDING_FLAGS = 1 << 2;

    // This is not treated as invisibility flag, but adds as a hint for an incomplete transition.
    // When the wallpaper animation runs, it replaces this flag with a proper invisibility
    // flag, INVISIBLE_BY_PENDING_FLAGS only for the duration of that animation.
    public static final int PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION = 1 << 3;

    private static final int INVISIBLE_FLAGS =
        INVISIBLE_BY_STATE_HANDLER | INVISIBLE_BY_APP_TRANSITIONS | INVISIBLE_BY_PENDING_FLAGS;
    public static final int STATE_HANDLER_INVISIBILITY_FLAGS =
        INVISIBLE_BY_STATE_HANDLER | PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION;
    public static final int INVISIBLE_ALL =
        INVISIBLE_FLAGS | PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION;

    @Retention(SOURCE)
    @IntDef(
        flag = true,
        value = {INVISIBLE_BY_STATE_HANDLER, INVISIBLE_BY_APP_TRANSITIONS, INVISIBLE_BY_PENDING_FLAGS, PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION})
    public @interface InvisibilityFlags {
    }

    private final ArrayList<VariantDeviceProfile.OnDeviceProfileChangeListener> mDPChangeListeners =
        new ArrayList<>();
    private final ArrayList<MultiWindowModeChangedListener> mMultiWindowModeChangedListeners =
        new ArrayList<>();

    protected VariantDeviceProfile mDeviceProfile;
    protected SystemUiController mSystemUiController;

    private static final int ACTIVITY_STATE_STARTED = 1 << 0;
    private static final int ACTIVITY_STATE_RESUMED = 1 << 1;

    private static final int ACTIVITY_STATE_USER_ACTIVE = 1 << 2;

    @Retention(SOURCE)
    @IntDef(
        flag = true,
        value = {ACTIVITY_STATE_STARTED, ACTIVITY_STATE_RESUMED, ACTIVITY_STATE_USER_ACTIVE})
    public @interface ActivityFlags {
    }

    @ActivityFlags
    private int mActivityFlags;

    // When the recents animation is running, the visibility of the Launcher is managed by the
    // animation
    @InvisibilityFlags
    private int mForceInvisible;

    private final ViewCache mViewCache = new ViewCache();

    public ViewCache getViewCache() {
        return mViewCache;
    }

    public VariantDeviceProfile getDeviceProfile() {
        return mDeviceProfile;
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return null;
    }

    public static BaseActivity fromContext(Context context) {
        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }
        return ((BaseActivity) ((ContextWrapper) context).getBaseContext());
    }

    public SystemUiController getSystemUiController() {
        if (mSystemUiController == null) {
            mSystemUiController = new SystemUiController(getWindow());
        }
        return mSystemUiController;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mActivityFlags |= ACTIVITY_STATE_STARTED;
        super.onStart();
    }

    @Override
    protected void onResume() {
        mActivityFlags |= ACTIVITY_STATE_RESUMED | ACTIVITY_STATE_USER_ACTIVE;
        super.onResume();
    }

    @Override
    protected void onUserLeaveHint() {
        mActivityFlags &= ~ACTIVITY_STATE_USER_ACTIVE;
        super.onUserLeaveHint();
    }

    @Override
    protected void onStop() {
        mActivityFlags &= ~ACTIVITY_STATE_STARTED & ~ACTIVITY_STATE_USER_ACTIVE;
        mForceInvisible = 0;
        super.onStop();
    }

    @Override
    protected void onPause() {
        mActivityFlags &= ~ACTIVITY_STATE_RESUMED;
        super.onPause();

        // Reset the overridden sysui flags used for the task-swipe launch animation, we do this
        // here instead of at the end of the animation because the start of the new activity does
        // not happen immediately, which would cause us to reset to launcher's sysui flags and then
        // back to the new app (causing a flash)
        getSystemUiController().updateUiState(SystemUiController.UI_STATE_NORMAL, 0);
    }

    public boolean isStarted() {
        return (mActivityFlags & ACTIVITY_STATE_STARTED) != 0;
    }

    public boolean hasBeenResumed() {
        return (mActivityFlags & ACTIVITY_STATE_RESUMED) != 0;
    }

    public boolean isUserActive() {
        return (mActivityFlags & ACTIVITY_STATE_USER_ACTIVE) != 0;
    }

    public void addOnDeviceProfileChangeListener(VariantDeviceProfile.OnDeviceProfileChangeListener listener) {
        mDPChangeListeners.add(listener);
    }

    public void removeOnDeviceProfileChangeListener(VariantDeviceProfile.OnDeviceProfileChangeListener listener) {
        mDPChangeListeners.remove(listener);
    }

    protected void dispatchDeviceProfileChanged() {
        for (int i = mDPChangeListeners.size() - 1; i >= 0; i--) {
            mDPChangeListeners.get(i).onDeviceProfileChanged(mDeviceProfile);
        }
    }

    public void addMultiWindowModeChangedListener(MultiWindowModeChangedListener listener) {
        mMultiWindowModeChangedListeners.add(listener);
    }

    public void removeMultiWindowModeChangedListener(MultiWindowModeChangedListener listener) {
        mMultiWindowModeChangedListeners.remove(listener);
    }

    public void addForceInvisibleFlag(@InvisibilityFlags int flag) {
        mForceInvisible |= flag;
    }

    public void clearForceInvisibleFlag(@InvisibilityFlags int flag) {
        mForceInvisible &= ~flag;
    }

    public boolean isForceInvisible() {
        return mForceInvisible != 0;
    }

    public interface MultiWindowModeChangedListener {
        void onMultiWindowModeChanged(boolean isInMultiWindowMode);
    }
}
