package bapale.rioc.unizon.screen

import bapale.rioc.unizon.FavoriteJoke
import bapale.rioc.unizon.JokeDao
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(jokeDao: JokeDao) {
    val favorites by jokeDao.getAllFavorites().collectAsState(initial = emptyList())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun shareJoke(joke: FavoriteJoke) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${joke.setup}\n\n${joke.punchline}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(favorites) { favJoke ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(favJoke.setup, style = MaterialTheme.typography.titleMedium)
                    Text(favJoke.punchline, style = MaterialTheme.typography.bodyMedium)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = { shareJoke(favJoke) }) {
                            Text("Share")
                        }
                        Button(onClick = { scope.launch { jokeDao.delete(favJoke) } }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}