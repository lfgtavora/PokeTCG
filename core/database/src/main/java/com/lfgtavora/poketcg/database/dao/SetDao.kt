package com.lfgtavora.poketcg.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetWithBoosters
import com.lfgtavora.poketcg.database.model.SetWithSerie
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {

    @Query("SELECT id, name, releaseDate, logo, card_count_total FROM sets ORDER BY releaseDate DESC LIMIT :itemsPerPage OFFSET :page * :itemsPerPage")
    fun getAll(
        page: Int,
        itemsPerPage: Int,
        orderBy: String? = "DESC",
        field: String? = "releaseDate"
    ): Flow<List<SetEntity>>

    @Transaction
    @Query("SELECT * FROM sets ORDER BY releaseDate DESC")
    fun getAllWithSeriePaginated(): PagingSource<Int, SetWithSerie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(sets: List<SetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(set: SetEntity)

    fun getAllSets(): Flow<List<SetEntity>>

}
