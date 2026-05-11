package edu.msudenver.cs3013.myfinalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.msudenver.cs3013.myfinalproject.model.PokemonDetail
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

/**
 * ViewModel responsible for fetching and managing
 * detailed Pokemon data for the detail screen.
 */
class PokemonDetailViewModel : ViewModel() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    sealed class UiState {
        // Loading state shown while fetching Pokemon details
        object Loading : UiState()
        // Success state containing Pokemon detail data
        data class Success(val pokemon: PokemonDetail) : UiState()
        // Error state containing failure message
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun fetchPokemonDetail(name: String) {
        viewModelScope.launch {
            try {
                val detail = client
                    .get("https://pokeapi.co/api/v2/pokemon/$name")
                    .body<PokemonDetail>()
                // Updates UI state with successful response
                _uiState.value = UiState.Success(detail)
            } catch (e: Exception) {
                // Updates UI state if request fails
                _uiState.value = UiState.Error("Failed to load $name details.")
            }
        }
    }
}