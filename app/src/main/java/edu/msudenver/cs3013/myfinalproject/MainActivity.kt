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
import edu.msudenver.cs3013.myfinalproject.ui.theme.MyFinalProjectTheme
import edu.msudenver.cs3013.myfinalproject.ui.theme.typeColor
import edu.msudenver.cs3013.myfinalproject.viewmodel.PokemonDetailViewModel
import edu.msudenver.cs3013.myfinalproject.viewmodel.PokemonViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinalProjectTheme {
                // NavController manages navigation between screens
                val navController = rememberNavController()

                // Creates and stores the ViewModel
                val viewModel: PokemonViewModel = viewModel()

                // Observes UI state changes from the ViewModel
                val uiState by viewModel.uiState.collectAsState()

                // NavHost defines all navigation routes in the app
                // startDestination: sets the first screen shown on launch
                NavHost(navController = navController, startDestination = "list") {

                    // List screen route
                    composable("list") {
                        when (uiState) {

                            // Show loading spinner while data is being fetched
                            is PokemonViewModel.UiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }

                            // Shows error message if network call fails
                            is PokemonViewModel.UiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = (uiState as PokemonViewModel.UiState.Error).message)
                                }
                            }

                            // Show the list when data loads successfully
                            is PokemonViewModel.UiState.Success -> {
                                val pokemon = (uiState as PokemonViewModel.UiState.Success).pokemon
                                // Pass the Pokemon list and navigation to the list screen
                                PokemonListScreen(pokemon) { name ->
                                    navController.navigate("detail/$name")
                                }
                            }
                        }
                    }

                    composable("detail/{name}") { backStackEntry ->
                        //Gets the Pokemon name passed through navigation arguments
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        //Displays the selected Pokemon's detail screen
                        PokemonDetailScreen(name)
                    }
                }
            }
        }
    }
}



/**
 * Displays a scrollable list of Pokemon
 *
 * @param pokemonList List of Pokémon retrieved from the API
 * @param onItemClick Callback triggered when a Pokémon is selected
 */
@Composable
fun PokemonListScreen(pokemonList: List<PokemonEntry>, onItemClick: (String) -> Unit) {
    // Displays a scrollable list of Pokemon
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pokemonList) { pokemon ->

            //Card for each Pokemon item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onItemClick(pokemon.name) },
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                //Centers Pokemon name inside the card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Displays Pokemon name with first letter capitalized
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() }
                    )
                }
            }
        }
    }
}



/**
 * Displays detailed information about a selected Pokemon,
 * including its image, stats, height, weight, and types.
 *
 * @param name Name of the selected Pokemon used to fetch detail data
 */
@Composable
fun PokemonDetailScreen(name: String) {
    // Each detail screen gets its own ViewModel instance
    val viewModel: PokemonDetailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Loads Pokemon details when the screen opens or when a different Pokemon name is received
    LaunchedEffect(name) {
        viewModel.fetchPokemonDetail(name)
    }

    when (uiState) {

        // Displays loading spinner while API data is being fetched
        is PokemonDetailViewModel.UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Displays error message if the request fails
        is PokemonDetailViewModel.UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((uiState as PokemonDetailViewModel.UiState.Error).message)
            }
        }

        // Displays Pokemon detail information after successful response
        is PokemonDetailViewModel.UiState.Success -> {
            val pokemon = (uiState as PokemonDetailViewModel.UiState.Success).pokemon

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Displays Pokemon name at the top
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Displays Pokemon sprite image from API URL
                pokemon.sprites.frontDefault?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                //Displays height and weight values
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(text = "Height: ${pokemon.height * 10} cm")
                    Text(text = "Weight: ${pokemon.weight / 10.0} kg")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Displays Pokemon type badges, each type displayed as a colored rounded card
                Text(
                    text = "Types",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pokemon.types.forEach { typeSlot ->
                        // Applies color styling based on pokemon type
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

                // Displays pokemon base stats using progress bars
                Text(
                    text = "Base Stats",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

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



/**
 * Displays a Pokemon stat with its value and a progress bar.
 *
 * @param statName Name of the stat being displayed
 * @param value Base stat value used for the progress bar
 */
@Composable
fun StatBar(statName: String, value: Int) {

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {

        // Displays stat name on the left and value on the right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = statName,
                fontSize = 12.sp
            )
            Text(
                text = value.toString(),
                fontSize = 12.sp
            )
        }

        // Progress bar representing the stat value (max possible base stat: 255)
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
        )
    }
}