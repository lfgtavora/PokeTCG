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

    @Query("SELECT * FROM cards WHERE setId = :setId")
    fun getAllCardsFromSet(setId: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE setId = :setId ORDER BY CAST(number AS INTEGER)")
    fun getCardsBySet(setId: String): PagingSource<Int, CardEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMany(cards: List<CardEntity>)

    @Query("SELECT COUNT(*) FROM cards WHERE setId = :setId")
    suspend fun getCardsCountBySet(setId: String): Int

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getCardById(id: String): Flow<CardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(card: CardEntity)

}