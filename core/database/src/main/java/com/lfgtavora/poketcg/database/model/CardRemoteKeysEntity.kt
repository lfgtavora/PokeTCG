package com.lfgtavora.poketcg.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_remote_keys",
    indices = [Index(value = ["setId"])],
)
data class CardRemoteKeysEntity(
    @PrimaryKey
    val cardId: String,
    val setId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
