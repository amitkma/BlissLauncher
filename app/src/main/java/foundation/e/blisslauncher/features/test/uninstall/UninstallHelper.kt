/*
 * Copyright 2022 Amit Kumar.
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
package foundation.e.blisslauncher.features.test.uninstall

import android.content.Context
import android.os.UserHandle
import android.os.UserManager
import android.util.ArrayMap
import foundation.e.blisslauncher.features.test.Alarm
import foundation.e.blisslauncher.features.test.OnAlarmListener

object UninstallHelper : OnAlarmListener {
    private const val CACHE_EXPIRE_TIMEOUT: Long = 5000
    private val mUninstallDisabledCache = ArrayMap<UserHandle, Boolean>(1)

    private val mCacheExpireAlarm: Alarm = Alarm()

    init {
        mCacheExpireAlarm.setOnAlarmListener(this)
    }

    fun isUninstallDisabled(user: UserHandle, context: Context): Boolean {
        var uninstallDisabled = mUninstallDisabledCache[user]
        if (uninstallDisabled == null) {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            val restrictions = userManager.getUserRestrictions(user)
            uninstallDisabled =
                (
                    restrictions.getBoolean(UserManager.DISALLOW_APPS_CONTROL, false) ||
                        restrictions.getBoolean(UserManager.DISALLOW_UNINSTALL_APPS, false)
                    )
            mUninstallDisabledCache[user] = uninstallDisabled
        }

        // Cancel any pending alarm and set cache expiry after some time
        mCacheExpireAlarm.setAlarm(CACHE_EXPIRE_TIMEOUT)
        return uninstallDisabled
    }

    override fun onAlarm(alarm: Alarm?) {
        mUninstallDisabledCache.clear()
    }
}
