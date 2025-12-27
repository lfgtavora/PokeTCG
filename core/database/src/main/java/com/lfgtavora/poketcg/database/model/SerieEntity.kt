package com.lfgtavora.poketcg.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serie")
data class SerieEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val logo: String? = null
)