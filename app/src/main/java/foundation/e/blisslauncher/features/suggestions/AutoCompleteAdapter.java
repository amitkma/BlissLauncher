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

package foundation.e.blisslauncher.features.suggestions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.features.test.TestActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoCompleteAdapter
    extends RecyclerView.Adapter<AutoCompleteAdapter.AutoCompleteViewHolder> {
  private List<String> mItems = new ArrayList<>();
  private final OnSuggestionClickListener mOnSuggestionClickListener;
  private final LayoutInflater mInflater;
  private String mQueryText;

  public AutoCompleteAdapter(Context context) {
    super();
    mOnSuggestionClickListener = (TestActivity) context;
    mInflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public AutoCompleteAdapter.AutoCompleteViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.item_suggestion, parent, false);
    AutoCompleteViewHolder holder = new AutoCompleteViewHolder(view);
    view.setOnClickListener(
        v -> mOnSuggestionClickListener.onClick(mItems.get(holder.getAdapterPosition())));
    return holder;
  }

  @Override
  public void onBindViewHolder(
      @NonNull AutoCompleteAdapter.AutoCompleteViewHolder holder, int position) {
    String suggestion = mItems.get(position);
    if (mQueryText != null) {
      SpannableStringBuilder spannable = new SpannableStringBuilder(suggestion);
      String lcSuggestion = suggestion.toLowerCase(Locale.getDefault());
      int queryTextPos = lcSuggestion.indexOf(mQueryText);
      while (queryTextPos >= 0) {
        spannable.setSpan(
            new StyleSpan(Typeface.BOLD),
            queryTextPos,
            queryTextPos + mQueryText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(
            new ForegroundColorSpan(Color.WHITE),
            queryTextPos,
            queryTextPos + mQueryText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        queryTextPos = lcSuggestion.indexOf(mQueryText, queryTextPos + mQueryText.length());
      }
      holder.mSuggestionTextView.setText(spannable);
    } else {
      holder.mSuggestionTextView.setText(suggestion);
    }
    setFadeAnimation(holder.itemView);
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void updateSuggestions(List<String> suggestions, String queryText) {
    mItems = suggestions;
    mQueryText = queryText;
    notifyDataSetChanged();
  }

  static class AutoCompleteViewHolder extends RecyclerView.ViewHolder {

    private TextView mSuggestionTextView;

    AutoCompleteViewHolder(View itemView) {
      super(itemView);
      mSuggestionTextView = itemView.findViewById(R.id.suggestionTextView);
    }
  }

  public interface OnSuggestionClickListener {
    void onClick(String suggestion);
  }

  private void setFadeAnimation(View view) {
    AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
    anim.setDuration(300);
    view.startAnimation(anim);
  }
}
