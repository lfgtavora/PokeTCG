package com.lfgtavora.poketcg.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.model.BoosterEntity
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.SerieEntity
import com.lfgtavora.poketcg.database.model.SetEntity

@Database(
    entities = [
        SetEntity::class,
        BoosterEntity::class,
        SerieEntity::class,
        CardEntity::class
    ],
    version = 1,
    exportSchema = true
)

internal abstract class PokeTcgDatabase: RoomDatabase() {
    abstract fun setDao(): SetDao
    abstract fun cardDao(): CardDao
}
