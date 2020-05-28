package foundation.e.blisslauncher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import foundation.e.blisslauncher.data.database.roomentity.WorkspaceItem

@Dao
abstract class LauncherDao {

    @RawQuery
    abstract fun getMaxIdInTable(query: SupportSQLiteQuery): Long

    @Insert
    abstract fun insert(workspaceItem: WorkspaceItem): Long

    @Insert
    abstract fun insertAll(workspaceItems: List<WorkspaceItem>)

    @Transaction
    open fun createEmptyDb() {
        dropWorkspaceItemTable()
        dropWorkspaceScreenTable()
    }

    @Query("SELECT * FROM launcherItems")
    abstract fun getAllWorkspaceItems(): List<WorkspaceItem>

    @Query("DELETE FROM launcherItems")
    abstract fun dropWorkspaceItemTable()

    @Query("DELETE FROM workspaceScreens")
    abstract fun dropWorkspaceScreenTable()
}