package bapale.rioc.unizon.screen

import bapale.rioc.unizon.FavoriteJoke
import bapale.rioc.unizon.JokeDao
import bapale.rioc.unizon.api.Joke
import bapale.rioc.unizon.api.RetrofitInstance
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun JokeScreen(jokeDao: JokeDao) {
    var joke by remember { mutableStateOf<Joke?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun loadJoke() {
        scope.launch {
            isLoading = true
            try {
                joke = RetrofitInstance.jokeService.getRandomJoke()
            } catch (e: Exception) {
                // Gérer l'erreur
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (joke == null) loadJoke()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { loadJoke() }) {
                Text("Refresh")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                joke?.let { currentJoke ->
                    Card(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Category: ${currentJoke.type}", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = currentJoke.setup, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = currentJoke.punchline, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                scope.launch {
                                    jokeDao.insert(
                                        FavoriteJoke(
                                            currentJoke.id,
                                            currentJoke.setup,
                                            currentJoke.punchline
                                        )
                                    )
                                    snackbarHostState.showSnackbar("Joke saved to favorites!")
                                }
                            }) {
                                Spacer(Modifier.width(8.dp))
                                Text("Add to Favorites")
                            }
                        }
                    }
                }
            }
        }
    }
}