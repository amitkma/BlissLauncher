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

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.customviews.BlissFrameLayout;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.features.suggestions.AutoCompleteAdapter;
import foundation.e.blisslauncher.features.suggestions.SuggestionsResult;
import foundation.e.blisslauncher.features.test.TestActivity;
import io.reactivex.observers.DisposableObserver;
import java.util.ArrayList;

public class SearchInputDisposableObserver extends DisposableObserver<SuggestionsResult> {

  private AutoCompleteAdapter networkSuggestionAdapter;
  private TestActivity launcherActivity;
  private ViewGroup appSuggestionsViewGroup;

  public SearchInputDisposableObserver(
      TestActivity activity, RecyclerView.Adapter autoCompleteAdapter, ViewGroup viewGroup) {
    this.networkSuggestionAdapter = (AutoCompleteAdapter) autoCompleteAdapter;
    this.launcherActivity = activity;
    this.appSuggestionsViewGroup = viewGroup;
  }

  @Override
  public void onNext(SuggestionsResult suggestionsResults) {
    if (suggestionsResults.type == SuggestionsResult.TYPE_NETWORK_ITEM) {
      networkSuggestionAdapter.updateSuggestions(
          suggestionsResults.getNetworkItems(), suggestionsResults.queryText);
    } else if (suggestionsResults.type == SuggestionsResult.TYPE_LAUNCHER_ITEM) {
      ((ViewGroup) appSuggestionsViewGroup.findViewById(R.id.suggestedAppGrid)).removeAllViews();
      appSuggestionsViewGroup.findViewById(R.id.openUsageAccessSettings).setVisibility(View.GONE);
      appSuggestionsViewGroup.findViewById(R.id.suggestedAppGrid).setVisibility(View.VISIBLE);
      for (LauncherItem launcherItem : suggestionsResults.getLauncherItems()) {
        BlissFrameLayout blissFrameLayout = launcherActivity.prepareSuggestedApp(launcherItem);
        launcherActivity.addAppToGrid(
            appSuggestionsViewGroup.findViewById(R.id.suggestedAppGrid), blissFrameLayout);
      }
    } else {
      launcherActivity.refreshSuggestedApps(appSuggestionsViewGroup, true);
      networkSuggestionAdapter.updateSuggestions(new ArrayList<>(), suggestionsResults.queryText);
    }
  }

  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void onComplete() {}
}
