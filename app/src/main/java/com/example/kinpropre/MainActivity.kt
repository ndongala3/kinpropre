package com.example.kinpropre

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kinpropre.ui.theme.KinPropreTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KinPropreTheme {
                MainScreenContent()
            }
        }
    }
}

@Composable
fun MainScreenContent(modifier: Modifier = Modifier) { // Ajout d'un modificateur optionnel
    val context = LocalContext.current

    Column(
        modifier = modifier // Appliquer le modificateur passé en argument
            .fillMaxSize() // Occuper tout l'espace disponible
            .padding(16.dp), // Ajouter un peu de marge intérieure
        horizontalAlignment = Alignment.CenterHorizontally, // Centrer les éléments horizontalement
        verticalArrangement = Arrangement.Center // Centrer les éléments verticalement
    ) {
        Text(
            text = "Bienvenue dans DechetsManager !",
            style = MaterialTheme.typography.headlineSmall // Utiliser un style de texte du thème
        )

        Spacer(modifier = Modifier.height(24.dp)) // Ajouter un espace vertical

        Button(onClick = {
            val intent = Intent(context, SubmissionActivity::class.java)
            // Vous pouvez passer des données supplémentaires à l'activité via intent.putExtra(...) si nécessaire :
            // intent.putExtra("USER_ID", "12345")
            context.startActivity(intent)
        }) {
            Text("Nouvelle Soumission de Déchet")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vous pouvez ajouter d'autres boutons ou éléments ici pour d'autres fonctionnalités
        Button(onClick = {
            // Action pour un autre bouton, par exemple "Voir mes soumissions"
            // val intent = Intent(context, MySubmissionsActivity::class.java)
            // context.startActivity(intent)
        }) {
            Text("Voir mes soumissions (Exemple)")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    KinPropreTheme {
        MainScreenContent()
    }
}

/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DechetsManagerTheme {
        Greeting("Android")
    }
}

 */