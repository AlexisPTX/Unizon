package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListIntScreen() {
    val items = (0..30).toList()
    LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
        items(items) { num ->
            Card(Modifier.fillMaxWidth().padding(4.dp)) {
                Text("Élément: $num", Modifier.padding(16.dp))
            }
        }
    }
}
