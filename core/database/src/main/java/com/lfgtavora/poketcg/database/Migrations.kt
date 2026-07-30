package com.lfgtavora.poketcg.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE cards ADD COLUMN sortNumber INTEGER NOT NULL DEFAULT 0"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_cards_setId_sortNumber_number` " +
                "ON `cards` (`setId`, `sortNumber`, `number`)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `card_remote_keys` (
                `cardId` TEXT NOT NULL,
                `setId` TEXT NOT NULL,
                `prevKey` INTEGER,
                `nextKey` INTEGER,
                PRIMARY KEY(`cardId`)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_card_remote_keys_setId` " +
                "ON `card_remote_keys` (`setId`)"
        )
        // Discard stale card cache so sortNumber is rebuilt from network.
        db.execSQL("DELETE FROM cards")
        db.execSQL("DELETE FROM card_remote_keys")
    }
}
