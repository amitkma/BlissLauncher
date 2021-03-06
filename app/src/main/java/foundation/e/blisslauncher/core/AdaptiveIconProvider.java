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

package foundation.e.blisslauncher.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.core.content.res.ResourcesCompat;
import foundation.e.blisslauncher.core.customviews.AdaptiveIconDrawableCompat;
import foundation.e.blisslauncher.core.utils.ResourceUtils;
import org.xmlpull.v1.XmlPullParser;

/** Created by falcon on 19/4/18. */
public class AdaptiveIconProvider {

  private static final String TAG = "AdaptiveIconProvider";

  private static final String[] IC_DIRS = new String[] {"mipmap", "drawable"};
  private static final String[] IC_CONFIGS = new String[] {"-anydpi-v26", "-v26", ""};

  public Drawable load(Context context, String packageName) {
    if (context == null) {
      throw new IllegalStateException(
          "Loader.with(Context) must be called before loading an icon.");
    }

    PackageManager packageManager = context.getPackageManager();
    Drawable background = null, foreground = null;

    try {
      Resources resources = packageManager.getResourcesForApplication(packageName);
      Resources.Theme theme = resources.newTheme();
      ResourceUtils.setFakeConfig(resources, 26); // Build.VERSION_CODES.O = 26

      AssetManager assetManager = resources.getAssets();

      XmlResourceParser manifestParser;
      String iconName = null;
      int iconId = 0;
      try {
        manifestParser = assetManager.openXmlResourceParser("AndroidManifest.xml");
        int eventType;

        String matcher = "application";
        while ((eventType = manifestParser.nextToken()) != XmlPullParser.END_DOCUMENT) {
          if (eventType == XmlPullParser.START_TAG && manifestParser.getName().equals(matcher)) {
            Log.d(TAG, "Manifest Parser Count: " + manifestParser.getAttributeCount());

            for (int i = 0; i < manifestParser.getAttributeCount(); i++) {
              Log.d(TAG, "Icon parser: " + manifestParser.getAttributeName(i));
              if (manifestParser.getAttributeName(i).equalsIgnoreCase("icon")) {
                iconId = Integer.parseInt(manifestParser.getAttributeValue(i).substring(1));
                Log.d(TAG, "Iconid:" + iconId);
                break;
              }
            }
            if (iconId != 0) {
              iconName = resources.getResourceName(iconId);
              Log.d("AdaptiveIcon", "Iconname: " + iconName);
              if (iconName.contains("/")) {
                iconName = iconName.split("/")[1];
              }
              break;
            } else {
              matcher = "activity";
            }
          }
        }
        manifestParser.close();
      } catch (Exception ignored) {
      }

      XmlResourceParser parser = null;
      if (iconId != 0) {
        try {
          parser = resources.getXml(iconId);
        } catch (Resources.NotFoundException e) {
          e.printStackTrace();
          parser = null;
        }
      }

      /*for (int dir = 0; dir < IC_DIRS.length && parser == null; dir++) {
          for (int config = 0; config < IC_CONFIGS.length && parser == null; config++) {
              for (String name : (iconName != null && !iconName.equals("ic_launcher"))
                      ? new String[]{iconName, "ic_launcher", "ic_launcher_round"} : new String[]{"ic_launcher", "ic_launcher_round"}) {
                  try {
                      String path = "res/" + IC_DIRS[dir] + IC_CONFIGS[config] + "/" + name
                              + ".xml";
                      Log.i(TAG, "path: " + path);
                      parser = assetManager.openXmlResourceParser(path);
                  } catch (Exception e) {
                      e.printStackTrace();
                  }

                  if (parser != null) {
                      break;
                  }
              }
          }
      }*/

      int backgroundRes = -1, foregroundRes = -1;
      if (parser != null) {
        int event;
        while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
          Log.i(
              TAG,
              packageName + ":parserName: " + parser.getName() + " " + parser.getAttributeCount());
          if (event == XmlPullParser.START_TAG) {
            switch (parser.getName()) {
              case "background":
                try {
                  backgroundRes =
                      parser.getAttributeResourceValue(
                          "http://schemas.android.com/apk/res/android", "drawable", 0);
                } catch (Exception e) {
                  try {
                    backgroundRes =
                        parser.getAttributeResourceValue(
                            "http://schemas.android.com/apk/res/android", "mipmap", 0);
                  } catch (Exception ignored) {
                  }
                }
                break;
              case "foreground":
                try {
                  foregroundRes =
                      parser.getAttributeResourceValue(
                          "http://schemas.android.com/apk/res/android", "drawable", 0);
                } catch (Exception e) {
                  try {
                    foregroundRes =
                        parser.getAttributeResourceValue(
                            "http://schemas.android.com/apk/res/android", "mipmap", 0);
                  } catch (Exception ignored) {
                  }
                }
                break;
            }
          }
          parser.next();
        }

        parser.close();
      }

      if (backgroundRes != -1) {
        Log.d(TAG, "BackgroundRes: " + backgroundRes);
        Log.d(TAG, "BackgroundResName: " + resources.getResourceName(backgroundRes));
        try {
          background = ResourcesCompat.getDrawable(resources, backgroundRes, theme);
        } catch (Resources.NotFoundException e) {
          e.printStackTrace();
          /*try {
              background = ResourcesCompat.getDrawable(resources,
                      resources.getIdentifier("ic_launcher_background", "mipmap",
                              packageName), theme);
          } catch (Resources.NotFoundException e1) {
              try {
                  background = ResourcesCompat.getDrawable(resources,
                          resources.getIdentifier("ic_launcher_background", "drawable",
                                  packageName), theme);
              } catch (Resources.NotFoundException ignored) {
              }
          }*/
        }
      }

      if (foregroundRes != -1) {
        try {
          foreground = ResourcesCompat.getDrawable(resources, foregroundRes, theme);
        } catch (Resources.NotFoundException e) {
          e.printStackTrace();
          /*try {
              foreground = ResourcesCompat.getDrawable(resources,
                      resources.getIdentifier("ic_launcher_foreground", "mipmap",
                              packageName), theme);
          } catch (Resources.NotFoundException e1) {
              try {
                  foreground = ResourcesCompat.getDrawable(resources,
                          resources.getIdentifier("ic_launcher_foreground", "drawable",
                                  packageName), theme);
              } catch (Resources.NotFoundException ignored) {
              }
          }*/
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (foreground != null && background != null) {
      return new AdaptiveIconDrawableCompat(background, foreground);
    } else {
      return null;
    }
  }
}
