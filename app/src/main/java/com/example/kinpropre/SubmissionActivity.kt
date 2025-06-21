package com.example.kinpropre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Pour obtenir le ViewModel via by viewModels()
import com.example.kinpropre.Data.SubmissionViewModel // Adaptez le chemin si nécessaire
import com.example.kinpropre.gui.SubmissionScreen // Adaptez le chemin si nécessaire
import com.example.kinpropre.ui.theme.KinPropreTheme // Adaptez le chemin si nécessaire
import com.example.kinpropre.ui.theme.KinPropreTheme

class SubmissionActivity : ComponentActivity() {

    // Obtenir une instance de SubmissionViewModel.
    // Cela nécessite la dépendance 'androidx.activity:activity-compose' et
    // 'androidx.lifecycle:lifecycle-viewmodel-compose' si vous utilisez viewModel() dans le Composable,
    // ou 'androidx.fragment:fragment-ktx' pour by viewModels() dans l'Activity.
    // Si vous utilisez Hilt, ce serait @AndroidEntryPoint et injecté différemment.
    private val submissionViewModel: SubmissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KinPropreTheme { // Appliquer votre thème Compose
                // Passer l'instance du ViewModel à votre écran Composable
                SubmissionScreen(viewModel = submissionViewModel)
            }
        }
    }
}