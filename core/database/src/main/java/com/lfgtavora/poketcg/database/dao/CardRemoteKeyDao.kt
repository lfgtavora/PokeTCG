package com.lfgtavora.poketcg.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lfgtavora.poketcg.database.model.CardRemoteKeysEntity

@Dao
interface CardRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<CardRemoteKeysEntity>)

    @Query("SELECT * FROM card_remote_keys WHERE cardId = :cardId")
    suspend fun remoteKeysCardId(cardId: String): CardRemoteKeysEntity?

    @Query("DELETE FROM card_remote_keys WHERE setId = :setId")
    suspend fun clearRemoteKeysBySet(setId: String)
}
