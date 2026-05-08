package edu.msudenver.cs3013.myfinalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import edu.msudenver.cs3013.myfinalproject.model.PokemonEntry
import edu.msudenver.cs3013.myfinalproject.model.PokemonListResponse
import edu.msudenver.cs3013.myfinalproject.ui.theme.MyFinalProjectTheme
import edu.msudenver.cs3013.myfinalproject.viewmodel.PokemonDetailViewModel
import edu.msudenver.cs3013.myfinalproject.viewmodel.PokemonViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
    val viewModel: PokemonDetailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(name) {
        viewModel.fetchPokemonDetail(name)
    }

    when (uiState) {
        is PokemonDetailViewModel.UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PokemonDetailViewModel.UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((uiState as PokemonDetailViewModel.UiState.Error).message)
            }
        }
        is PokemonDetailViewModel.UiState.Success -> {
            val pokemon = (uiState as PokemonDetailViewModel.UiState.Success).pokemon

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Sprite
                pokemon.sprites.frontDefault?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Height & Weight
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(text = "Height: ${pokemon.height * 10} cm")
                    Text(text = "Weight: ${pokemon.weight / 10.0} kg")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Types
                Text(text = "Types", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.forEach { typeSlot ->
                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(
                                containerColor = typeColor(typeSlot.type.name)
                            )
                        ) {
                            Text(
                                text = typeSlot.type.name.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats
                Text(text = "Base Stats", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                pokemon.stats.forEach { statSlot ->
                    StatBar(
                        statName = statSlot.stat.name.uppercase(),
                        value = statSlot.baseStat
                    )
                }
            }
        }
    }
}

@Composable
fun StatBar(statName: String, value: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = statName, fontSize = 12.sp)
            Text(text = value.toString(), fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
        )
    }
}

fun typeColor(type: String): Color {
    return when (type) {
        "fire"     -> Color(0xFFF08030)
        "water"    -> Color(0xFF6890F0)
        "grass"    -> Color(0xFF78C850)
        "electric" -> Color(0xFFF8D030)
        "psychic"  -> Color(0xFFF85888)
        "ice"      -> Color(0xFF98D8D8)
        "dragon"   -> Color(0xFF7038F8)
        "dark"     -> Color(0xFF705848)
        "fairy"    -> Color(0xFFEE99AC)
        "fighting" -> Color(0xFFC03028)
        "poison"   -> Color(0xFFA040A0)
        "ground"   -> Color(0xFFE0C068)
        "rock"     -> Color(0xFFB8A038)
        "bug"      -> Color(0xFFA8B820)
        "ghost"    -> Color(0xFF705898)
        "steel"    -> Color(0xFFB8B8D0)
        "flying"   -> Color(0xFF98A8F0)
        "normal"   -> Color(0xFFA8A878)
        else       -> Color.Gray
    }
}