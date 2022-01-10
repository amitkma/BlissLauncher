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

package foundation.e.blisslauncher.features.test;

import foundation.e.blisslauncher.core.database.model.LauncherItem;

/** Utility object to manage the occupancy in a grid. */
public class GridOccupancy {

  private final int mCountX;
  private final int mCountY;

  // We use 1-d array for cells because we use index of the cell than x and y.
  public final boolean[] cells;

  public GridOccupancy(int countX, int countY) {
    mCountX = countX;
    mCountY = countY;
    cells = new boolean[countX * countY];
  }

  public void markCells(int index, boolean value) {
    if (index < 0 || index >= mCountX * mCountY) return;
    cells[index] = value;
  }

  public void markCells(CellAndSpan cell, boolean value) {
    markCells(cell.cellY * mCountX + cell.cellX, value);
  }

  public void markCells(LauncherItem item, boolean value) {
    markCells(item.cell, value);
  }

  public void clear() {
    for (int i = 0; i < mCountX * mCountY; i++) {
      markCells(i, false);
    }
  }
}
