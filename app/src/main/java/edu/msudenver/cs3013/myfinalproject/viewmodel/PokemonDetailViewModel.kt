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

class PokemonDetailViewModel : ViewModel() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val pokemon: PokemonDetail) : UiState()
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
                _uiState.value = UiState.Success(detail)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load $name details.")
            }
        }
    }
}