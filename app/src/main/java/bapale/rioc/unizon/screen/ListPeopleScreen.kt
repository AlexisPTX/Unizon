package bapale.rioc.unizon.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Person(val prenom:String, val nom:String, val tel:String, val age:Int, val sexe:String)

@Composable
fun ListPeopleScreen() {
    val people = listOf(
        Person("Alice","Durand","0612345678",28,"F"),
        Person("Bob","Martin","0798765432",31,"M"),
        Person("Camille","Cerf","0123456789",20,"O")
    )
    LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
        items(people) { p ->
            val bg = when (p.sexe) {
                "F" -> Color(0xFFFFE0E0)
                "M" -> Color(0xFFCCE5FF)
                else -> Color.LightGray
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = bg)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "${p.prenom} ${p.nom}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(p.tel, style = MaterialTheme.typography.bodyMedium)
                        Text("Age: ${p.age}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
