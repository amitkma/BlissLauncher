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

package foundation.e.blisslauncher.core.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
  private static OkHttpClient sOkHttpClient;
  private static GsonConverterFactory sGsonConverterFactory = GsonConverterFactory.create();
  private static RxJava2CallAdapterFactory sRxJava2CallAdapterFactory =
      RxJava2CallAdapterFactory.create();

  static {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    sOkHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
  }

  public static Retrofit getInstance(String url) {
    return new Retrofit.Builder()
        .baseUrl(url)
        .client(sOkHttpClient)
        .addCallAdapterFactory(sRxJava2CallAdapterFactory)
        .addConverterFactory(sGsonConverterFactory)
        .build();
  }
}
