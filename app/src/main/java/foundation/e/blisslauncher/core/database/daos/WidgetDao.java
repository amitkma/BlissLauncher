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
import foundation.e.blisslauncher.core.database.model.WidgetItem;
import java.util.List;

@Dao
public interface WidgetDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(WidgetItem widgetItem);

  @Query("SELECT height FROM widget_items WHERE id = :id")
  int getHeight(int id);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<WidgetItem> widgetItems);

  @Query("DELETE FROM widget_items WHERE id = :id")
  void delete(int id);
}
