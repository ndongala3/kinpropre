package com.example.kinpropre.gui

import androidx.core.content.ContextCompat
import android.Manifest // Pour la demande de permission de localisation (si non gérée ailleurs)
import android.annotation.SuppressLint
import androidx.activity.viewModels
import android.content.Context // Utilisé implicitement par LocalContext.current
import android.net.Uri
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button // Ou androidx.compose.material3.Button si vous utilisez Material 3
import androidx.compose.material.Text // Ou androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider // Pour FileProvider.getUriForFile
// Importez votre ViewModel et votre Thème
import com.example.kinpropre.Data.SubmissionViewModel // Adaptez le chemin si nécessaire
import com.example.kinpropre.ui.theme.KinPropreTheme // Adaptez le chemin si nécessaire
// Imports pour Google Maps Compose (assurez-vous d'avoir la dépendance)
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
//import com.google.maps.android.compose.CameraUpdateFactory
import java.io.File
import java.util.UUID


//@SuppressLint("UnrememberedMutableState")
@Composable
fun SubmissionScreen(
    // Il est préférable d'obtenir le ViewModel via hiltViewModel() ou viewModel()
    // plutôt que de le passer en paramètre directement, sauf si vous avez une raison spécifique.
    // Exemple : viewModel: SubmissionViewModel = viewModel()
    viewModel: SubmissionViewModel
) {
    val context = LocalContext.current
    // Collecter les états du ViewModel
    val imageUri by viewModel.imageUri.collectAsState()
    val location by viewModel.location.collectAsState() // Assurez-vous que viewModel.location est un StateFlow<Location?>

    // État pour stocker l'URI du fichier temporaire pour l'appareil photo
    var tempFileUri by remember { mutableStateOf<Uri?>(null) } // Renommé pour plus de clarté

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // L'image a été sauvegardée dans tempFileUri
                viewModel.setImageUri(tempFileUri)
            }
            // Vous pourriez vouloir gérer le cas 'else' (l'utilisateur a annulé)
        }
    )

    // Fonction pour créer un fichier temporaire et obtenir son URI via FileProvider
    fun createTempImageFileUri(context: Context): Uri {
        val timeStamp = System.currentTimeMillis() // Pour un nom de fichier unique
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = context.cacheDir // Ou context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) si vous voulez un stockage plus persistant (et gérer les permissions)
        val imageFile = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg",       /* suffix */
            storageDir    /* directory */
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Assurez-vous que cela correspond à votre authorities dans le Manifest
            imageFile
        )
    }

    fun launchCamera() {
        val newFileUri = createTempImageFileUri(context)
        tempFileUri = newFileUri // Stocker l'URI avant de lancer l'appareil photo
        cameraLauncher.launch(newFileUri)
    }

    // Gestionnaire de permission pour la localisation (exemple)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // La permission est accordée, obtenir la localisation
                viewModel.getCurrentLocation(context)
            } else {
                // La permission est refusée. Gérer ce cas (afficher un message, etc.)
                // Log.d("SubmissionScreen", "Location permission denied")
            }
        }
    )

    fun requestLocationPermissionAndFetch() {
        // Vérifier si la permission est déjà accordée
        // (Cette vérification est déjà dans viewModel.getCurrentLocation, mais c'est bien de l'avoir ici aussi pour la logique de l'UI)
        // Vous pourriez déplacer la logique de demande de permission entièrement dans le ViewModel ou la garder ici.
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                viewModel.getCurrentLocation(context)
            }
            else -> {
                // Demander la permission
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize() // Pour occuper tout l'écran
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Pour permettre le défilement si le contenu dépasse
    ) {
        Button(onClick = { launchCamera() }) {
            Text("📸 Prendre une photo")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { requestLocationPermissionAndFetch() }) {
            Text("📍 Obtenir la localisation")
        }

        Spacer(Modifier.height(16.dp))

        imageUri?.let { uri ->
            Text("Image URI: ${uri.toString()}") // Afficher l'URI complet pour le débogage
            // Vous pourriez vouloir afficher l'image ici en utilisant Coil ou Glide
            // AsyncImage(model = uri, contentDescription = "Image capturée", modifier = Modifier.height(200.dp))
        }

        location?.let { loc ->
            Spacer(Modifier.height(16.dp))
            Text("Coordonnées : ${loc.latitude}, ${loc.longitude}")

            // Mettre à jour la position de la caméra lorsque la localisation change
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(loc.latitude, loc.longitude), 15f)
            }
            // Si vous voulez que la carte se recentre lorsque `loc` change :
            LaunchedEffect(loc) {/*x
                cameraPositionState.animate(
                    com.google.maps.android.compose.CameraUpdateFactory.newLatLngZoom(
                        LatLng(loc.latitude, loc.longitude),
                        15f
                    )
                )
            */}


            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState // Utilisez cameraPositionState ici
            ) {
                Marker(
                    state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                    title = "Votre position"
                )
            }

            Spacer(Modifier.height(16.dp))

            // Désactiver le bouton d'envoi si l'image ou la localisation est manquante
            val canUpload = imageUri != null && location != null
            Button(
                onClick = {
                    if (canUpload) { // Double vérification, bien que le bouton soit désactivé
                        viewModel.uploadData(context)
                        // Peut-être afficher un indicateur de chargement ici
                        // et naviguer ou afficher un message de succès/échec basé sur le résultat du ViewModel
                    }
                },
                enabled = canUpload // Activer/désactiver le bouton
            ) {
                Text("📤 Envoyer")
            }
        }
    }
}