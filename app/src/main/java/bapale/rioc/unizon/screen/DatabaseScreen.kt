package bapale.rioc.unizon.screen

import bapale.rioc.unizon.AppDatabase
import bapale.rioc.unizon.User
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun DatabaseScreen() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "users.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    val dao = db.userDao()
    val scope = rememberCoroutineScope()
    val users by dao.getAll().collectAsState(initial = emptyList())

    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = prenom,
            onValueChange = { prenom = it },
            label = { Text("Prénom") }
        )
        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom") }
        )
        Button(onClick = {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                context,
                { _, y, m, d ->
                    // Format YYYY-MM-DD
                    birth = "%04d-%02d-%02d".format(y, m + 1, d)
                },
                year, month, day
            ).show()
        }) {
            Text(if (birth.isBlank()) "Choisir une date" else "Date : $birth")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (prenom.isNotBlank() && nom.isNotBlank() && birth.isNotBlank()) {
                val user = User(prenom = prenom, nom = nom, birthDate = birth)

                scope.launch {
                    dao.insert(user)
                }

                prenom = ""; nom = ""; birth = ""
            }
        }) { Text("Enregistrer") }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { u ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "${u.prenom} ${u.nom}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Date de naissance : ${u.birthDate}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
