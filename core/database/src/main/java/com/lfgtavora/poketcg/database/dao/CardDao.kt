package com.lfgtavora.poketcg.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lfgtavora.poketcg.database.model.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT id, name, image, category, localId FROM cards WHERE setId = :setId")
    fun getAllCardsFromSet(setId: String): Flow<List<CardEntity>>

    @Query("SELECT id, name, image, category, localId FROM cards WHERE setId = :setId")
    fun getAllCardsFromSetPaginated(setId: String): PagingSource<Int, CardEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<CardEntity>)

}