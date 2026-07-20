package com.lfgtavora.poketcg.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetWithCardsPreviews
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {

    @Query("SELECT * FROM sets ORDER BY releaseDate DESC")
    fun pagingSource(): PagingSource<Int, SetEntity>

    @Query("SELECT * FROM sets ORDER BY releaseDate DESC LIMIT :itemsPerPage OFFSET ((:page - 1) * :itemsPerPage)")
    fun getAll(
        page: Int,
        itemsPerPage: Int
    ): Flow<List<SetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(sets: List<SetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: SetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(set: SetEntity)

    @Query("SELECT * FROM sets WHERE id = :id")
    fun getById(id: String): Flow<SetEntity>

    @Transaction
    @Query("SELECT * FROM sets WHERE id = :id")
    fun getSetWithCards(id: String): Flow<SetWithCardsPreviews?>

    @Query("DELETE FROM sets")
    suspend fun clearAll()

}
