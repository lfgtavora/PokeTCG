package com.lfgtavora.poketcg.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lfgtavora.poketcg.database.converter.CardTypeConverters
import com.lfgtavora.poketcg.database.dao.CardDao
import com.lfgtavora.poketcg.database.dao.SetDao
import com.lfgtavora.poketcg.database.dao.SetRemoteKeyDao
import com.lfgtavora.poketcg.database.model.CardEntity
import com.lfgtavora.poketcg.database.model.SetEntity
import com.lfgtavora.poketcg.database.model.SetRemoteKeysEntity

@Database(
    entities = [
        SetEntity::class,
        CardEntity::class,
        SetRemoteKeysEntity::class,
     ],
    version = 2,
    exportSchema = true
)
@TypeConverters(CardTypeConverters::class)
abstract class PokeTcgDatabase : RoomDatabase() {
    abstract fun setDao(): SetDao
    abstract fun cardDao(): CardDao
    abstract fun setRemoteKeyDao(): SetRemoteKeyDao
}
