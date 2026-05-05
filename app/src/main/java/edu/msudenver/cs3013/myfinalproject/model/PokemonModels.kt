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