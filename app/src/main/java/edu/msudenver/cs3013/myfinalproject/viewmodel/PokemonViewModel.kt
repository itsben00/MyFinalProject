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

/**
 * ViewModel responsible for fetching Pokémon data
 * and managing UI state for the Pokémon list screen.
 */
class PokemonViewModel : ViewModel() {

    //HTTP client configured for API requests
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // UI states
    sealed class UiState {
        //Loading state shown while fetching state
        object Loading : UiState()
        //Success state containing pokemon list data
        data class Success(val pokemon: List<PokemonEntry>) : UiState()
        //Error state containing failure message
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchPokemonList()
    }

    //Fetches pokemon list data from PokeAPI
    private fun fetchPokemonList() {
        viewModelScope.launch {
            try {
                val results = client
                    .get("https://pokeapi.co/api/v2/pokemon?limit=151")
                    .body<PokemonListResponse>()
                    //Retrieves pokemon list from response
                    .results
                //Updates UI state with successful results
                _uiState.value = UiState.Success(results)

            } catch (e: Exception) {
                //Updates UI state if request fails
                _uiState.value = UiState.Error("Failed to load Pokémon. Check your connection.")
            }
        }
    }
}