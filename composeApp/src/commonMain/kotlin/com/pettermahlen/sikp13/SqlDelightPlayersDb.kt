package com.pettermahlen.sikp13

import com.pettermahlen.sikp13.db.AppDatabase
import com.pettermahlen.sikp13.db.Players
import com.pettermahlen.sikp13.SkillLevel.*

class SqlDelightPlayersDb(
    private val database: AppDatabase
) : PlayersDb {
    override fun listPlayers(): List<Player> =
        database.playersQueries
            .listPlayers()
            .executeAsList()
            .map { player ->
                Player(
                    name = player.name,
                    skillLevel = SkillLevel.valueOf(player.skill_level)
                )
            }

    override fun insertPlayer(player: Player) {
        database.playersQueries.insertPlayer(
            name = player.name,
            skill_level = player.skillLevel.name
        )
    }

    override fun getPlayer(name: String): Player? =
        database.playersQueries
            .getPlayer(name)
            .executeAsOneOrNull()
            ?.let { player ->
                Player(
                    name = player.name,
                    skillLevel = SkillLevel.valueOf(player.skill_level)
                )
            }

    override fun updatePlayer(player: Player) {
        database.playersQueries.updatePlayer(
            skill_level = player.skillLevel.name,
            name = player.name
        )
    }

    override fun deletePlayer(name: String) {
        database.playersQueries.deletePlayer(name)
    }

    override fun isEmpty(): Boolean =
        database.playersQueries.listPlayers().executeAsList().isEmpty()

    fun insertInitialPlayers(password: String) {
        val encryptedData = "BAAAFUMZAQgBHDAGDxcIHGUrDxIMHU8rGQkEHQZGJyQpOTonYCAAHQ4YSigfAwcLAwUMBEMvKzI0ei4EHg4DUCcLBg1BPSouIzQgei4SDw1NPAoZEAIXCQEZAQBBPSouIzQgei0LGRUEEQFKIQABHAYERiksIitgKAQDGg4HAw9NMR0fBgADEQEeAgAAXCorOThnMwoZCxNNMQgPGAMIAghGJyQpOTonYCIFER0GAwRNIgAOAgQBGQEORiksIitgKQ4BGQFKIxIIHgYLRiwoNCY/J2spFRceDxNNJwYGBggeXCIvLig4PWUvAxcEHk8hCw0BGQFGIiA/NGUvBggMGE8tqdcZFRwZBQ9BPSouIzQgeioEBAgCUCILEBsMUCQGDwwEXCcrOCVnNgYGAxFNOA4NDw8PHA4ORiwoNCY/J2srGQMDGkEns9kEGRICHkMiKzMpeigLCBMEFQNKKAQfFwMfBAVBNS45M2slFQEEAw8KUC0YAw8GXCorOThnOAoEBAgDF08/Bg8IA0MiKzMpeiYcCw9NJAoNBAADFAoYSjYIGQEYDwgOGEMvKzI0eiUFAgADHgoZSjMCFwYEGQoEXCorOThnOgACBEEgs8oCBqLEHkMnLyUkJSJgIRQeEQZKIgAAGQsDRiwoNCY/J2shFQpKIQgDFA0FB00lMT0uYC0IH08oAKLbAgMDBAZBPSouIzQgeiILHhUEA08nBRMBEQEOSjICHKzOBAZBPSouIzQgeiIFCwkMHQtKIhQeAwoDBE0oMTwzYC4fHA4EDg5NPgoECQkYXCIvLig4PWUlGQoMAk8PDQRNNgYEDggGXCcrOCVnPxseBUEoGQsPHAABHEMvKzI0ej0fCAQDUCcPGA8MHgsPEE0gNSsjPyxnIw4HHwQBUD2pzwUPFR0NRiwoNCY/J2s5EQMCC0ElEQYODxNBNS45M2s0HxoZDwdNIwcLGQkEBEMvKzI0UA==" 
        
        val decryptedCsv = PlayerListEncryption.decryptPlayerList(encryptedData, password)
        val players = parseCSV(decryptedCsv)
        players.forEach { player ->
            insertPlayer(player)
        }
    }
} 