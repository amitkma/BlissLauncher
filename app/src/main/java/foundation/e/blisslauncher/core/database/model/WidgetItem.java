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

package foundation.e.blisslauncher.core.database.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "widget_items")
public class WidgetItem {

  @PrimaryKey public int id;
  public int height;

  public WidgetItem() {}

  public WidgetItem(int id, int height) {
    this.id = id;
    this.height = height;
  }
}
