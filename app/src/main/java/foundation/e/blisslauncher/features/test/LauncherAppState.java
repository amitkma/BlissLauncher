/*
 * Copyright (C) 2013 The Android Open Source Project
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

import static foundation.e.blisslauncher.core.utils.SecureSettingsObserver.newNotificationSettingsObserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;
import foundation.e.blisslauncher.core.ConfigMonitor;
import foundation.e.blisslauncher.core.UserManagerCompat;
import foundation.e.blisslauncher.core.executors.MainThreadExecutor;
import foundation.e.blisslauncher.core.utils.Preconditions;
import foundation.e.blisslauncher.core.utils.SecureSettingsObserver;
import foundation.e.blisslauncher.features.notification.NotificationListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class LauncherAppState {

    /** Hidden field Settings.Secure.NOTIFICATION_BADGING */
    public static final String NOTIFICATION_BADGING = "notification_badging";
    /** Hidden field Settings.Secure.ENABLED_NOTIFICATION_LISTENERS */
    private static final String NOTIFICATION_ENABLED_LISTENERS = "enabled_notification_listeners";

    public static final String ACTION_FORCE_ROLOAD = "force-reload-launcher";

    // We do not need any synchronization for this variable as its only written on UI thread.
    private static LauncherAppState INSTANCE;

    private final Context mContext;
    private final InvariantDeviceProfile mInvariantDeviceProfile;

    private final SecureSettingsObserver mNotificationDotsObserver;
    private TestActivity launcher;
    private LauncherModel mModel;

    public static LauncherAppState getInstance(final Context context) {
        if (INSTANCE == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                INSTANCE = new LauncherAppState(context.getApplicationContext());
            } else {
                try {
                    return new MainThreadExecutor().submit(new Callable<LauncherAppState>() {
                        @Override
                        public LauncherAppState call() throws Exception {
                            return LauncherAppState.getInstance(context);
                        }
                    }).get();
                } catch (InterruptedException|ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return mContext;
    }

    private LauncherAppState(Context context) {

        Log.v(TestActivity.TAG, "LauncherAppState initiated");
        Preconditions.assertUIThread();
        mContext = context;

        mInvariantDeviceProfile = new InvariantDeviceProfile(mContext);

        mModel = new LauncherModel(this);
        // Register intent receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        // For handling managed profiles
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_ADDED);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_REMOVED);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_AVAILABLE);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_UNLOCKED);
        mContext.registerReceiver(mModel, filter);

        UserManagerCompat.getInstance(mContext).enableAndResetCache();
        new ConfigMonitor(mContext).register();

        // Register an observer to rebind the notification listener when dots are re-enabled.
        mNotificationDotsObserver =
            newNotificationSettingsObserver(mContext, this::onNotificationSettingsChanged);
        mNotificationDotsObserver.register();
        mNotificationDotsObserver.dispatchOnChange();
    }

    protected void onNotificationSettingsChanged(boolean areNotificationDotsEnabled) {
        if (areNotificationDotsEnabled) {
            NotificationListener.requestRebind(new ComponentName(
                mContext, NotificationListener.class));
        }
    }

    public TestActivity getLauncher() {
        return launcher;
    }

    /**
     * Call from Application.onTerminate(), which is not guaranteed to ever be called.
     */
    public void onTerminate() {
        mContext.unregisterReceiver(mModel);
        if (mNotificationDotsObserver != null) {
            mNotificationDotsObserver.unregister();
        }
    }

    LauncherModel setLauncher(TestActivity launcher) {
        this.launcher = launcher;
        mModel.initialize(launcher);
        return mModel;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return mInvariantDeviceProfile;
    }

    /**
     * Shorthand for {@link #getInvariantDeviceProfile()}
     */
    public static InvariantDeviceProfile getIDP(Context context) {
        return LauncherAppState.getInstance(context).getInvariantDeviceProfile();
    }


}
