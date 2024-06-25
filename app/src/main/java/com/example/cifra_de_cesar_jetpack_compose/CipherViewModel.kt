package com.example.cifra_de_cesar_jetpack_compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CipherViewModel: ViewModel() {


    // Cipher UI state
    private val _uiState = MutableStateFlow(CipherUiState())
    val uiState: StateFlow<CipherUiState> = _uiState.asStateFlow()

    var inputedMessage by mutableStateOf("")
        private set

    var cipherKey by mutableStateOf("Key")
        private set


    fun showDialogBoxMessage() {
        _uiState.value = CipherUiState(dialogBoxMessage = true)
    }
    fun closeDialogBoxMessage() {
        _uiState.value = CipherUiState(dialogBoxMessage = false)
    }

    fun updateInputedMessage(_message: String) {
        inputedMessage = _message
    }
    fun updateCipherKey(_key: String) {
        cipherKey = _key
    }


    /*
    fun checkInputedKey(): Boolean {

        if (keyEncrypt.toInt() in 1..26) {
            //updateCifraState(inputedMessage, keyEncrypt)
            return false
        } else if (keyEncrypt.equals("")) {
            return true
        } else {
            return true
        }
    }
     */

    fun checkInputedKey(cipherKey: String) {

        if (cipherKey.isEmpty()) {
            _uiState.value = CipherUiState(isInvalidKey = true)
        } else {
            val keyInt = cipherKey.toIntOrNull()
            if (keyInt == null || keyInt !in 1..26) {
                _uiState.value = CipherUiState(isInvalidKey = true)
            } else {
                _uiState.value = CipherUiState(isInvalidKey = false)
                _uiState.value = CipherUiState(isSuccessfullyEncrypted = true)
            }
        }
    }

    // When showing a toast, reset the state so that the app doesn't show multiple toasts
    fun resetErrorState() {
        _uiState.value = _uiState.value.copy(
            isInvalidKey = false,
            isSuccessfullyEncrypted = false
        )
    }

    fun checkInputedMessage() {
        if (!inputedMessage.isEmpty()) {
            updateCifraState(inputedMessage, cipherKey)
        }
    }

    private fun updateCifraState(inputedMessage: String, key: String) {
        _uiState.update { currentState ->
            currentState.copy(
                message = inputedMessage,
                dialogBoxMessage = false
            )
        }
    }

    fun runEncryptMessage(message: String, cipherKey: String) {
        checkInputedKey(cipherKey)

        if (!_uiState.value.isInvalidKey) {

            _uiState.value = CipherUiState(isSuccessfullyEncrypted = true)
            val keyEcryptInt = cipherKey.toInt()

            val cipher = Cipher()
            cipher.makeAlphabet(keyEcryptInt)

            val encryptedMessage = cipher.encryptMessage(message)
            updateInputedMessage(encryptedMessage)
            updateCifraState(encryptedMessage, cipherKey)

        }
    }
}