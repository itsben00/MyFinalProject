package edu.msudenver.cs3013.myfinalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.msudenver.cs3013.myfinalproject.model.PokemonEntry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import edu.msudenver.cs3013.myfinalproject.model.PokemonListResponse

class PokemonViewModel : ViewModel() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // UI states
    sealed class UiState {
        object Loading : UiState()
        data class Success(val pokemon: List<PokemonEntry>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchPokemonList()
    }

    private fun fetchPokemonList() {
        viewModelScope.launch {
            try {
                val results = client
                    .get("https://pokeapi.co/api/v2/pokemon?limit=151")
                    .body<PokemonListResponse>()
                    .results
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load Pokémon. Check your connection.")
            }
        }
    }
}