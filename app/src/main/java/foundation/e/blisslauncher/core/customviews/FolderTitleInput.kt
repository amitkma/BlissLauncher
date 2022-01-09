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
package foundation.e.blisslauncher.core.customviews

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import foundation.e.blisslauncher.features.test.UiThreadHelper

class FolderTitleInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BlissInput(context, attrs) {

    private var mShowImeAfterFirstLayout = false
    private var mForceDisableSuggestions = false

    /**
     * Implemented by listeners of the back key.
     */
    interface OnBackKeyListener {
        fun onBackKey(): Boolean
    }

    private var mBackKeyListener: OnBackKeyListener? = null

    fun setOnBackKeyListener(listener: OnBackKeyListener) {
        mBackKeyListener = listener
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        // If this is a back key, propagate the key back to the listener
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            mBackKeyListener?.onBackKey() ?: false
        } else super.onKeyPreIme(keyCode, event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mShowImeAfterFirstLayout) {
            // soft input only shows one frame after the layout of the EditText happens,
            post {
                showSoftInput()
                mShowImeAfterFirstLayout = false
            }
        }
    }

    fun showKeyboard() {
        mShowImeAfterFirstLayout = !showSoftInput()
    }

    fun hideKeyboard() {
        UiThreadHelper.hideKeyboardAsync(context, windowToken)
    }

    private fun showSoftInput(): Boolean {
        return requestFocus() &&
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun dispatchBackKey() {
        hideKeyboard()
        mBackKeyListener?.onBackKey()
    }

    /**
     * Set to true when you want isSuggestionsEnabled to return false.
     * Use this to disable the red underlines that appear under typos when suggestions is enabled.
     */
    fun forceDisableSuggestions(forceDisableSuggestions: Boolean) {
        mForceDisableSuggestions = forceDisableSuggestions
    }

    override fun isSuggestionsEnabled(): Boolean {
        return !mForceDisableSuggestions && super.isSuggestionsEnabled()
    }

    fun reset() {
        if (!TextUtils.isEmpty(text)) {
            setText("")
        }
        if (isFocused) {
            val nextFocus = focusSearch(FOCUS_DOWN)
            nextFocus?.requestFocus()
        }
        hideKeyboard()
    }
}
