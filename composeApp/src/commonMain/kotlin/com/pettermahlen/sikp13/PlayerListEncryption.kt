package com.pettermahlen.sikp13

object PlayerListEncryption {
    private fun deriveKey(password: String): ByteArray {
        if (password.isEmpty()) {
            throw IllegalArgumentException("Password cannot be empty")
        }
        return password.encodeToByteArray()
    }

    private fun xorWithKey(data: ByteArray, key: ByteArray): ByteArray {
        val result = ByteArray(data.size)
        for (i in data.indices) {
            result[i] = (data[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return result
    }

    fun encryptPlayerList(csvData: String, password: String): String {
        try {
            val bytes = csvData.encodeToByteArray()
            val key = deriveKey(password)
            val encrypted = xorWithKey(bytes, key)
            return encrypted.encodeToBase64()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to encrypt player list: ${e.message}")
        }
    }

    fun decryptPlayerList(encryptedData: String, password: String): String {
        try {
            if (!encryptedData.matches(Regex("^[A-Za-z0-9+/=]*$"))) {
                throw IllegalArgumentException("Invalid Base64 format")
            }
            
            val encrypted = encryptedData.decodeBase64()
            val key = deriveKey(password)
            val decrypted = xorWithKey(encrypted, key)
            return decrypted.decodeToString()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decrypt player list: ${e.message}")
        }
    }
}

private fun ByteArray.encodeToBase64(): String {
    val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val padding = '='
    val output = StringBuilder()
    var i = 0
    
    while (i < size) {
        val b1 = this[i].toInt() and 0xFF
        val b2 = if (i + 1 < size) this[i + 1].toInt() and 0xFF else 0
        val b3 = if (i + 2 < size) this[i + 2].toInt() and 0xFF else 0
        
        output.append(table[b1 shr 2])
        output.append(table[((b1 and 0x03) shl 4) or (b2 shr 4)])
        
        if (i + 1 < size) {
            output.append(table[((b2 and 0x0F) shl 2) or (b3 shr 6)])
        } else {
            output.append(padding)
        }
        
        if (i + 2 < size) {
            output.append(table[b3 and 0x3F])
        } else {
            output.append(padding)
        }
        
        i += 3
    }
    
    return output.toString()
}

private fun String.decodeBase64(): ByteArray {
    val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val output = mutableListOf<Byte>()
    var buffer = 0
    var bitsLeft = 0
    
    for (c in this) {
        if (c == '=') break
        
        val value = table.indexOf(c)
        if (value == -1) continue
        
        buffer = (buffer shl 6) or value
        bitsLeft += 6
        
        if (bitsLeft >= 8) {
            bitsLeft -= 8
            output.add(((buffer shr bitsLeft) and 0xFF).toByte())
        }
    }
    
    return output.toByteArray()
} 