package edu.msudenver.cs3013.myfinalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import edu.msudenver.cs3013.myfinalproject.model.PokemonEntry
import edu.msudenver.cs3013.myfinalproject.model.PokemonListResponse
import edu.msudenver.cs3013.myfinalproject.ui.theme.MyFinalProjectTheme
import edu.msudenver.cs3013.myfinalproject.viewmodel.PokemonViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import androidx.compose.foundation.clickable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private suspend fun fetchPokemonList(): List<PokemonEntry> =
        client.get("https://pokeapi.co/api/v2/pokemon?limit=150")
            .body<PokemonListResponse>()
            .results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinalProjectTheme {
                val navController = rememberNavController()
                val viewModel: PokemonViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                NavHost(navController = navController, startDestination = "list"){
                    composable("list"){
                        when(uiState) {
                            is PokemonViewModel.UiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is PokemonViewModel.UiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = (uiState as PokemonViewModel.UiState.Error).message)
                                }
                            }
                            is PokemonViewModel.UiState.Success -> {
                                val pokemon = (uiState as PokemonViewModel.UiState.Success).pokemon
                                PokemonListScreen(pokemon) { name ->
                                    navController.navigate("detail/$name")
                                }
                            }
                        }
                    }
                    composable("detail/{name}") { backStackEntry ->
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        PokemonDetailScreen(name)
                    }
                }


            }
        }
    }
}

@Composable
fun PokemonListScreen(pokemonList: List<PokemonEntry>, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pokemonList) { pokemon ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onItemClick(pokemon.name) },
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Detail screen for: ${name.replaceFirstChar { it.uppercase() }}")
    }
}