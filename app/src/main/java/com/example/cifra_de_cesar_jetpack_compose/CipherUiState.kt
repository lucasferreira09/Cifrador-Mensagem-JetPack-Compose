package com.example.cifra_de_cesar_jetpack_compose

data class CipherUiState(

    val message: String = "",
    val key: String = "",
    val dialogBoxMessage: Boolean = false,
    val isInvalidKey: Boolean = false,
    val isSuccessfullyEncrypted: Boolean = false
)
