package com.lfgtavora.poketcg.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity

@Dao
interface SetRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<SetRemoteKeysEntity>)

    @Query("SELECT * FROM set_remote_keys WHERE setId = :setId")
    suspend fun remoteKeysSetId(setId: String): SetRemoteKeysEntity?

    @Query("DELETE FROM set_remote_keys")
    suspend fun clearRemoteKeys()
}
