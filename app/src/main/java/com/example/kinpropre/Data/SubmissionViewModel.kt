package com.example.kinpropre.Data
/*
class SubmissionViewModel {
}
*/

import android.Manifest // Pour Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager // Pour PackageManager.PERMISSION_GRANTED
import android.location.Location // Pour le type de _location
import android.net.Uri // Pour le type de _imageUri
import androidx.core.app.ActivityCompat // Pour checkSelfPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Pour lancer les coroutines liées au ViewModel
import com.google.android.gms.location.LocationServices // Pour getFusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow // Si vous utilisez .asStateFlow() pour exposer les StateFlows
import kotlinx.coroutines.launch // Pour viewModelScope.launch
import okhttp3.MediaType.Companion.toMediaType // Pour "text/plain".toMediaType()
import okhttp3.MediaType.Companion.toMediaTypeOrNull // Pour "image/*".toMediaTypeOrNull()
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit // Pour Retrofit.Builder()
import retrofit2.converter.gson.GsonConverterFactory // Pour GsonConverterFactory.create()
import java.io.File // Pour créer un objet File à partir du chemin de l'image


class SubmissionViewModel : ViewModel() {
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }


    fun getCurrentLocation(context: Context) {
        // Il est crucial de s'assurer que cette méthode est appelée UNIQUEMENT APRÈS
        // que l'UI a confirmé et obtenu la permission.
        // La vérification ici est une double sécurité, mais la logique principale
        // de demande/octroi de permission doit être gérée par l'appelant (l'UI).

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Idéalement, ce cas ne devrait jamais être atteint si l'UI gère correctement les permissions.
            // Vous pourriez logger une erreur ici ou notifier l'UI d'une manière ou d'une autre.
            // Par exemple, émettre un événement d'erreur que l'UI peut observer.
            // _locationError.value = "Permission de localisation non accordée." // Exemple avec un StateFlow d'erreur
            // Log.e("ViewModelLocation", "getCurrentLocation appelé sans permission !")
            return // Ne pas continuer sans permission
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // location peut être null ici !
                if (location != null) {
                    _location.value = location
                } else {
                    // lastLocation est null.
                    // Gérer ce cas :
                    // 1. Informer l'utilisateur (par exemple, "Impossible d'obtenir la localisation actuelle, veuillez vérifier vos paramètres de localisation.")
                    // 2. Envisager de demander des mises à jour de localisation (plus complexe, nécessite un LocationCallback)
                    //    Log.w("ViewModelLocation", "Last known location is null.")
                    //    requestNewLocationData(context, fusedLocationClient) // Vous créeriez cette fonction
                    _location.value = null // S'assurer que l'état est explicitement null
                    // Vous pourriez vouloir émettre un message d'erreur spécifique pour l'UI ici.
                }
            }
            .addOnFailureListener { e ->
                // Gérer l'échec de l'obtention de la dernière localisation
                // Log.e("ViewModelLocation", "Erreur lors de l'obtention de la localisation.", e)
                _location.value = null // Réinitialiser ou gérer l'état d'erreur
                // Vous pourriez vouloir émettre un message d'erreur spécifique pour l'UI ici.
            }
    }

    fun uploadData(context: Context) {
        val image = _imageUri.value ?: return
        val loc = _location.value ?: return

        val file = File(image.path ?: return)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

        val latPart = loc.latitude.toString().toRequestBody("text/plain".toMediaType())
        val lonPart = loc.longitude.toString().toRequestBody("text/plain".toMediaType())

        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-backend.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.uploadData(photoPart, latPart, lonPart)
                if (response.isSuccessful) {
                    // 💡 Tu peux afficher une Snackbar de succès ici
                } else {
                    // ⚠️ Affiche une erreur avec le code
                }
            } catch (e: Exception) {
                // 🌐 Gestion d’erreur réseau
            }
        }
    }
}