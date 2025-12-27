package com.lfgtavora.poketcg.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "boosters",
    foreignKeys = [
        ForeignKey(
            entity = SetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
        )
    ]
)
data class BoosterEntity(
    @PrimaryKey
    val id: String,
    val setId: String,
    val name: String,
    val logo: String? = null,
    val artFront: String? = null,
    val artBack: String? = null
)