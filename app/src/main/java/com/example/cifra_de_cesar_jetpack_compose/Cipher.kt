package com.example.cifra_de_cesar_jetpack_compose

class Cipher() {
    private val originalAlphabet = mutableListOf<String>() // More Important
    private val caesarAlphabet = mutableListOf<String>()   // More Important

    fun makeAlphabet(key: Int) {
        val initialAlphabet = "abcdefghijklmnopqrstuvwxyz"
        val partEncrypted = initialAlphabet.substring(key, 26);
        val encryptAlphabet = partEncrypted + initialAlphabet.substring(0, key);

        //Original Alphabet
        initialAlphabet.forEach {
            originalAlphabet.add(it.toString())
        }

        //Cypher Alphabet
        encryptAlphabet.forEach {
            caesarAlphabet.add(it.toString())
        }
    }

    fun encryptMessage(message: String): String {
        var encryptedMessage = ""

        message.forEach {

            //if it has blank spaces, add blank spaces
            if (it.equals(" ")) {
                encryptedMessage += " ";
            }

            val index = originalAlphabet.indexOf(it.toString())
            //if it's a unknown letter/number, add the same thing
            if (index == -1) {
                encryptedMessage += it;
            } else {
                encryptedMessage += caesarAlphabet[index] // Otherwise, the encrypted letter is added
            }
        }
        return encryptedMessage
    }
}

