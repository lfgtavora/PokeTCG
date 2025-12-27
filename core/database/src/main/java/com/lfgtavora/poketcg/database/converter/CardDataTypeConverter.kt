package com.lfgtavora.poketcg.database.converter

import androidx.room.TypeConverter
import com.lfgtavora.poketcg.database.model.AttackData
import com.lfgtavora.poketcg.database.model.ResistanceData
import com.lfgtavora.poketcg.database.model.WeaknessData
import kotlinx.serialization.json.Json

class CardTypeConverters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromAttackList(value: List<AttackData>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toAttackList(value: String?): List<AttackData>? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromWeaknessList(value: List<WeaknessData>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toWeaknessList(value: String?): List<WeaknessData>? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromResistanceList(value: List<ResistanceData>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toResistanceList(value: String?): List<ResistanceData>? =
        value?.let { json.decodeFromString(it) }

}