/*
 * Copyright 2022 Amit Kumar.
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

/**
 * Base class which represents an area on the grid.
 */
public class CellAndSpan {

    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    public CellAndSpan() {
    }

    public void copyFrom(CellAndSpan copy) {
        cellX = copy.cellX;
        cellY = copy.cellY;
    }

    public CellAndSpan(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
    }

    public String toString() {
        return "(" + cellX + ", " + cellY + ")";
    }
}
