package com.pettermahlen.sikp13

interface PlayersDb {
    // Create
    fun insertPlayer(player: Player)
    
    // Read
    fun listPlayers(): List<Player>
    fun getPlayer(name: String): Player?
    
    // Update
    fun updatePlayer(player: Player)
    
    // Delete
    fun deletePlayer(name: String)
    
    // Utility
    fun isEmpty(): Boolean
} 