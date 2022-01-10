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

package foundation.e.blisslauncher.features.folder;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.core.touch.ItemClickHandler;
import foundation.e.blisslauncher.core.touch.ItemLongClickListener;
import foundation.e.blisslauncher.features.test.IconTextView;
import foundation.e.blisslauncher.features.test.VariantDeviceProfile;
import java.util.ArrayList;
import java.util.List;

public class FolderPagerAdapter extends PagerAdapter {

  private Context mContext;
  private List<LauncherItem> mFolderAppItems;
  private VariantDeviceProfile mDeviceProfile;
  private List<GridLayout> grids = new ArrayList<>();

  public FolderPagerAdapter(
      Context context, List<LauncherItem> items, VariantDeviceProfile mDeviceProfile) {
    this.mContext = context;
    this.mFolderAppItems = items;
    this.mDeviceProfile = mDeviceProfile;
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position) {
    GridLayout viewGroup =
        (GridLayout) LayoutInflater.from(mContext).inflate(R.layout.apps_page, container, false);
    viewGroup.setRowCount(3);
    viewGroup.setColumnCount(3);
    viewGroup.setPadding(
        mContext.getResources().getDimensionPixelSize(R.dimen.folder_padding),
        mContext.getResources().getDimensionPixelSize(R.dimen.folder_padding),
        mContext.getResources().getDimensionPixelSize(R.dimen.folder_padding),
        mContext.getResources().getDimensionPixelSize(R.dimen.folder_padding));
    ViewPager.LayoutParams params = (ViewPager.LayoutParams) viewGroup.getLayoutParams();
    params.width = GridLayout.LayoutParams.WRAP_CONTENT;
    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
    int i = 0;
    while (9 * position + i < mFolderAppItems.size() && i < 9) {
      LauncherItem appItem = mFolderAppItems.get(9 * position + i);
      IconTextView appView =
          (IconTextView) LayoutInflater.from(mContext).inflate(R.layout.app_icon, null, false);
      appView.applyFromShortcutItem(appItem);
      appView.setTextVisibility(true);
      appView.setOnClickListener(ItemClickHandler.INSTANCE);
      appView.setOnLongClickListener(ItemLongClickListener.INSTANCE_WORKSPACE);
      GridLayout.LayoutParams iconLayoutParams = new GridLayout.LayoutParams();
      iconLayoutParams.height =
          mDeviceProfile.getCellHeightPx() + mDeviceProfile.getIconDrawablePaddingPx() * 2;
      iconLayoutParams.width = mDeviceProfile.getCellHeightPx();
      iconLayoutParams.setGravity(Gravity.CENTER);
      appView.setLayoutParams(iconLayoutParams);
      viewGroup.addView(appView);
      i++;
    }
    grids.add(viewGroup);
    container.addView(viewGroup);
    return viewGroup;
  }

  @Override
  public int getCount() {
    return (int) Math.ceil((float) mFolderAppItems.size() / 9);
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }

  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
  }
}
