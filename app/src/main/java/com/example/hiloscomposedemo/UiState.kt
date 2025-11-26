package com.example.hiloscomposedemo

// Esta clase guarda el "estado" que vamos a mostrar en la pantalla.
// message: texto principal.
// threadName: nombre del hilo (para ver si fue Main o Worker).
data class UiState(
    val message: String = "Pulsa un botón para empezar",
    val threadName: String = ""
)
