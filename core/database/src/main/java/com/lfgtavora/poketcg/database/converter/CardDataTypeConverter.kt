package com.lfgtavora.poketcg.database.converter

import androidx.room.TypeConverter
import com.lfgtavora.poketcg.database.model.AbilityData
import com.lfgtavora.poketcg.database.model.AncientTraitData
import com.lfgtavora.poketcg.database.model.AttackData
import com.lfgtavora.poketcg.database.model.ResistanceData
import com.lfgtavora.poketcg.database.model.WeaknessData
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class CardTypeConverters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(ListSerializer(String.serializer()), it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { json.decodeFromString(ListSerializer(String.serializer()), it) }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { json.encodeToString(ListSerializer(Int.serializer()), it) }
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let { json.decodeFromString(ListSerializer(Int.serializer()), it) }
    }

    @TypeConverter
    fun fromAttackList(value: List<AttackData>?): String? {
        return value?.let { json.encodeToString(ListSerializer(AttackData.serializer()), it) }
    }

    @TypeConverter
    fun toAttackList(value: String?): List<AttackData>? {
        return value?.let { json.decodeFromString(ListSerializer(AttackData.serializer()), it) }
    }

    @TypeConverter
    fun fromWeaknessList(value: List<WeaknessData>?): String? {
        return value?.let { json.encodeToString(ListSerializer(WeaknessData.serializer()), it) }
    }

    @TypeConverter
    fun toWeaknessList(value: String?): List<WeaknessData>? {
        return value?.let { json.decodeFromString(ListSerializer(WeaknessData.serializer()), it) }
    }

    @TypeConverter
    fun fromResistanceList(value: List<ResistanceData>?): String? {
        return value?.let { json.encodeToString(ListSerializer(ResistanceData.serializer()), it) }
    }

    @TypeConverter
    fun toResistanceList(value: String?): List<ResistanceData>? =
        value?.let { json.decodeFromString(ListSerializer(ResistanceData.serializer()), it) }

    @TypeConverter
    fun fromAbilityList(value: List<AbilityData>?): String? {
        return value?.let { json.encodeToString(ListSerializer(AbilityData.serializer()), it) }
    }

    @TypeConverter
    fun toAbilityList(value: String?): List<AbilityData>? =
        value?.let { json.decodeFromString(ListSerializer(AbilityData.serializer()), it) }

    @TypeConverter
    fun fromAncientTrait(value: AncientTraitData?): String? {
        return value?.let { json.encodeToString(AncientTraitData.serializer(), it) }
    }

    @TypeConverter
    fun toAncientTrait(value: String?): AncientTraitData? =
        value?.let { json.decodeFromString(AncientTraitData.serializer(), it) }

}