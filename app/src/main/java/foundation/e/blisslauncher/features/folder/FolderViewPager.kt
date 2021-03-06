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

package foundation.e.blisslauncher.features.folder

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import foundation.e.blisslauncher.core.customviews.LauncherPagedView
import foundation.e.blisslauncher.core.database.model.LauncherItem

class FolderViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    fun getFirstItem(): View? {
        if (childCount < 1) {
            return null
        }
        val currContainer: GridLayout = getChildAt(currentItem) as GridLayout
        return if (currContainer.childCount > 0) {
            currContainer[0]
        } else {
            null
        }
    }

    fun getLastItem(): View? {
        if (childCount < 1) {
            return null
        }
        val currContainer: GridLayout = getChildAt(currentItem) as GridLayout
        val lastRank = currContainer.childCount - 1
        return if (currContainer.childCount > 0) {
            currContainer[lastRank]
        } else {
            null
        }
    }

    fun iterateOverItems(op: LauncherPagedView.ItemOperator): View? {
        for (k in 0 until childCount) {
            val page: GridLayout = getChildAt(k) as GridLayout
            for (j in 0 until page.childCount) {
                val v: View? = page.getChildAt(j)
                if (v != null && op.evaluate(v.tag as LauncherItem, v, j)) {
                    return v
                }
            }
        }
        return null
    }

    fun removeItem(v: View?) {
        for (i in childCount - 1 downTo 0) {
            val page: GridLayout = getChildAt(i) as GridLayout
            page.removeView(v)
        }
    }

    fun getItemCount(): Int {
        val lastPageIndex = childCount - 1
        return if (lastPageIndex < 0) {
            0
        } else {
            (getChildAt(lastPageIndex) as GridLayout).childCount + lastPageIndex * 9 // maxItems per page
        }
    }

    /**
     * Sets the focus on the first visible child.
     */
    fun setFocusOnFirstChild() {
        (getChildAt(currentItem) as ViewGroup?)?.getChildAt(0)?.requestFocus()
    }
}
