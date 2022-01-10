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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SearchSuggestionUtil {

  public SuggestionProvider getSuggestionProvider(Context context) {
    String defaultSearchEngine = defaultSearchEngine(context);
    if (defaultSearchEngine != null && defaultSearchEngine.length() > 0) {
      defaultSearchEngine = defaultSearchEngine.toLowerCase();
      if (defaultSearchEngine.contains("qwant")) {
        return new QwantProvider();
      } else {
        return new DuckDuckGoProvider();
      }
    } else {
      return new DuckDuckGoProvider();
    }
  }

  public Uri getUriForQuery(Context context, String query) {
    String defaultSearchEngine = defaultSearchEngine(context);
    if (defaultSearchEngine != null && defaultSearchEngine.length() > 0) {
      defaultSearchEngine = defaultSearchEngine.toLowerCase();
      if (defaultSearchEngine.contains("qwant")) {
        return Uri.parse("https://www.qwant.com/?q=" + query);
      } else if (defaultSearchEngine.contains("duckduckgo")) {
        return Uri.parse("https://duckduckgo.com/?q=" + query);
      } else {
        return Uri.parse("https://spot.ecloud.global/?q=" + query);
      }
    } else {
      return Uri.parse("https://spot.ecloud.global/?q=" + query);
    }
  }

  private String defaultSearchEngine(Context context) {
    ContentResolver contentResolver = context.getContentResolver();
    Uri uri =
        Uri.parse("content://foundation.e.browser.provider")
            .buildUpon()
            .appendPath("search_engine")
            .build();
    Cursor cursor = contentResolver.query(uri, null, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      return cursor.getString(0);
    } else {
      return "";
    }
  }
}
