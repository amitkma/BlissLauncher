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

package foundation.e.blisslauncher.core.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import foundation.e.blisslauncher.core.database.model.LauncherItem;
import foundation.e.blisslauncher.core.utils.Constants;
import java.util.List;

@Dao
public interface LauncherDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(LauncherItem launcherItem);

  @Query("SELECT * FROM launcher_items ORDER BY container, screen_id, cell")
  List<LauncherItem> getAllItems();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<LauncherItem> launcherItems);

  @Query("DELETE FROM launcher_items WHERE item_id = :id")
  void delete(String id);

  @Query(
      "DELETE FROM launcher_items WHERE title = :name and item_type = "
          + Constants.ITEM_TYPE_SHORTCUT)
  void deleteShortcut(String name);

  @Query("UPDATE launcher_items SET item_id = :newComponentName WHERE item_id = :id")
  int updateComponent(String id, String newComponentName);
}
