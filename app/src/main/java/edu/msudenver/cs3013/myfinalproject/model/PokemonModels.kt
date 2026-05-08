package edu.msudenver.cs3013.myfinalproject.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val results: List<PokemonEntry>
)

@Serializable
data class PokemonEntry(
    val name: String,
    val url: String
)

@Serializable
data class PokemonDetail(
    val name: String,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val height: Int,
    val weight: Int
)

@Serializable
data class Sprites(
    @SerialName("front_default")
    val frontDefault: String? = null
)

@Serializable
data class TypeSlot(
    val type: TypeInfo
)

@Serializable
data class TypeInfo(
    val name: String
)

@Serializable
data class StatSlot(
    @SerialName("base_stat")
    val baseStat: Int,
    val stat: StatInfo
)

@Serializable
data class StatInfo(
    val name: String
)