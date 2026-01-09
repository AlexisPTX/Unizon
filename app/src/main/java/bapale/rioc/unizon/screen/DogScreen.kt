package bapale.rioc.unizon.screen

import bapale.rioc.unizon.api.RetrofitInstance
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun DogScreen() {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadDog() {
        scope.launch {
            try {
                imageUrl = RetrofitInstance.dogService.getRandomDog().message
            } catch (e: Exception) {
                // Gérer l'erreur
            }
        }
    }

    LaunchedEffect(Unit) { if (imageUrl == null) loadDog() }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Random Dog",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                CircularProgressIndicator()
            }

            Button(onClick = { loadDog() }, modifier = Modifier.padding(top = 16.dp)) {
                Text("New Dog")
            }
        }
    }
}