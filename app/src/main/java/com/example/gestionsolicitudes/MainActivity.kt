package com.example.gestionsolicitudes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionsolicitudes.data.local.AppDatabase
import com.example.gestionsolicitudes.data.remote.ApiClient
import com.example.gestionsolicitudes.data.repository.SolicitudRepository
import com.example.gestionsolicitudes.navigation.AppNavigation
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModel
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = SolicitudRepository(database.solicitudDao(), ApiClient.apiService)
        val factory = SolicitudViewModelFactory(repository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: SolicitudViewModel = viewModel(factory = factory)
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}