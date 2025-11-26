package com.example.hiloscomposedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.hiloscomposedemo.ui.theme.HilosComposeDemoTheme

class MainActivity : ComponentActivity() {

    // Creamos el ViewModel asociado a esta Activity.
    // "by viewModels()" lo provee AndroidX.
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ejemplo de lifecycleScope en el hilo principal (Main Thread).
        lifecycleScope.launch {
            val threadName = Thread.currentThread().name
            Log.d("MainActivity", "lifecycleScope (Main) en hilo: $threadName")
        }

        // Ejemplo de lifecycleScope en un Worker Thread usando Dispatchers.IO.
        lifecycleScope.launch(Dispatchers.IO) {
            val threadName = Thread.currentThread().name
            Log.d("MainActivity", "lifecycleScope (IO) en hilo: $threadName")
        }

        setContent {
            // Usa el tema que te creó el proyecto.
            // Si antes el código decía otro nombre, pon el mismo.
            HilosComposeDemoTheme {
                // Pasamos el ViewModel a la pantalla Compose.
                MainScreen(viewModel = mainViewModel)
            }
        }
    }
}
