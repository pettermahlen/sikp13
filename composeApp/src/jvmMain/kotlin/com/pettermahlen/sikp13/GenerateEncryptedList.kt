package com.pettermahlen.sikp13

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: GenerateEncryptedList <password>")
        return
    }

    val password = args[0]
    
    // Read CSV data from stdin
    val csvData = generateSequence(::readLine).joinToString("\n")

    try {
        // Validate CSV format by parsing it (will throw if invalid)
        parseCSV(csvData)
        
        // Encrypt the CSV data
        val encrypted = PlayerListEncryption.encryptPlayerList(csvData, password)
        println(encrypted)
    } catch (e: Exception) {
        System.err.println("Error: ${e.message}")
        exitProcess(1)
    }
} 