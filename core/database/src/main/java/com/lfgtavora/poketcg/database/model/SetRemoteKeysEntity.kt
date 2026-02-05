package com.lfgtavora.poketcg.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "set_remote_keys")
data class SetRemoteKeysEntity(
    @PrimaryKey
    val setId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
