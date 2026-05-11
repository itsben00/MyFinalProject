package edu.msudenver.cs3013.myfinalproject.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//API response containing a list of pokemon
@Serializable
data class PokemonListResponse(
    val results: List<PokemonEntry>
)

//Basic pokemon item from the pokemon list endpoint
@Serializable
data class PokemonEntry(
    val name: String,
    val url: String
)

//Detailed pokemon information returned by API
@Serializable
data class PokemonDetail(
    val name: String,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val height: Int,
    val weight: Int
)

//Contains pokemon sprite image URLs
@Serializable
data class Sprites(
    @SerialName("front_default")
    val frontDefault: String? = null
)

//Pokemon type slot from the API response
@Serializable
data class TypeSlot(
    val type: TypeInfo
)

//Pokemon type details
@Serializable
data class TypeInfo(
    val name: String
)

//pokemon base stat entry
@Serializable
data class StatSlot(
    @SerialName("base_stat")
    val baseStat: Int,
    val stat: StatInfo
)

//Pokemon stat info
@Serializable
data class StatInfo(
    val name: String
)