package bapale.rioc.unizon.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun InputScreen() {
    var text by remember { mutableStateOf("") }
    var showSecond by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (!showSecond) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(text, { text = it }, label = { Text("Message") })
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (text.isBlank()) {
                    Toast.makeText(context, "Message vide", Toast.LENGTH_SHORT).show()
                } else showSecond = true
            }) { Text("Afficher") }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Deuxième écran", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text(text)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { showSecond = false }) { Text("Retour") }
        }
    }
}
