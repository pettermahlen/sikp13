package com.pettermahlen.sikp13

import kotlin.test.*

class PlayerListEncryptionTest {
  private val testCsv =
      "name,skill_level\n" +
          "Test Player 1,EASY\n" +
          "Test Player 2,MEDIUM\n" +
          "Test Player 3,HARD"

  @Test
  fun `encryption and decryption with simple password`() {
    val password = "test123"
    val encrypted = PlayerListEncryption.encryptPlayerList(testCsv, password)
    val decrypted = PlayerListEncryption.decryptPlayerList(encrypted, password)

    assertEquals(testCsv, decrypted)
  }

  @Test
  fun `encryption produces different output for different passwords`() {
    val password1 = "password1"
    val password2 = "password2"

    val encrypted1 = PlayerListEncryption.encryptPlayerList(testCsv, password1)
    val encrypted2 = PlayerListEncryption.encryptPlayerList(testCsv, password2)

    assertNotEquals(encrypted1, encrypted2)
  }

  @Test
  fun `decryption with wrong password fails`() {
    val correctPassword = "correct"
    val wrongPassword = "wrong"

    val encrypted = PlayerListEncryption.encryptPlayerList(testCsv, correctPassword)

    val decrypted = PlayerListEncryption.decryptPlayerList(encrypted, wrongPassword)
    assertNotEquals(decrypted, testCsv)
  }

  @Test
  fun `encryption with empty list works`() {
    val password = "test123"
    val emptyCsv = "name,skill_level"

    val encrypted = PlayerListEncryption.encryptPlayerList(emptyCsv, password)
    val decrypted = PlayerListEncryption.decryptPlayerList(encrypted, password)

    assertEquals(emptyCsv, decrypted)
  }

  @Test
  fun `encryption with empty password fails`() {
    val password = ""

    assertFailsWith<IllegalArgumentException>("Password cannot be empty") {
      PlayerListEncryption.encryptPlayerList(testCsv, password)
    }
  }

  @Test
  fun `decryption with empty password fails`() {
    val encrypted = PlayerListEncryption.encryptPlayerList(testCsv, "test123")

    assertFailsWith<IllegalArgumentException>("Password cannot be empty") {
      PlayerListEncryption.decryptPlayerList(encrypted, "")
    }
  }

  @Test
  fun `encryption preserves special characters in names`() {
    val specialCharsCsv =
        "name,skill_level\n" + "Åke Östlund,EASY\n" + "José García,MEDIUM\n" + "名前 名字,HARD"

    val password = "test123"
    val encrypted = PlayerListEncryption.encryptPlayerList(specialCharsCsv, password)
    val decrypted = PlayerListEncryption.decryptPlayerList(encrypted, password)

    assertEquals(specialCharsCsv, decrypted)
  }

  @Test
  fun `encryption handles names with commas and quotes`() {
    val complexCsv =
        "name,skill_level\n" +
            "\"Smith, John\",EASY\n" +
            "\"O'Brien \"\"Bobby\"\"\",MEDIUM\n" +
            "\"First,Last\"\"Quote\"\"\",HARD"

    val password = "test123"
    val encrypted = PlayerListEncryption.encryptPlayerList(complexCsv, password)
    val decrypted = PlayerListEncryption.decryptPlayerList(encrypted, password)

    assertEquals(complexCsv, decrypted)
  }

  @Test
  fun `encrypted data is base64 encoded`() {
    val password = "test123"
    val encrypted = PlayerListEncryption.encryptPlayerList(testCsv, password)

    // Base64 only contains alphanumeric characters, '+', '/', and '='
    assertTrue(encrypted.matches(Regex("^[A-Za-z0-9+/=]+$")))
  }

  @Test
  fun `decryption with invalid base64 fails`() {
    val invalidBase64 = "!@#$%^&*()"
    val password = "test123"

    assertFailsWith<IllegalArgumentException>("Invalid Base64 format") {
      PlayerListEncryption.decryptPlayerList(invalidBase64, password)
    }
  }
}
