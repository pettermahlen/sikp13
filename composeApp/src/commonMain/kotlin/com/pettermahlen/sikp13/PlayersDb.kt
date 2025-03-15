package com.pettermahlen.sikp13

import com.pettermahlen.sikp13.SkillLevel.EASY
import com.pettermahlen.sikp13.SkillLevel.HARD
import com.pettermahlen.sikp13.SkillLevel.MEDIUM

interface PlayersDb {
    fun listPlayers(): List<Player>
}

class HardwiredPlayersDb : PlayersDb {
    override fun listPlayers(): List<Player> = listOf(
        Player("Aesam Ashimi", MEDIUM),
        Player("Ammar Irshaidat", EASY),
        Player("Anton Hall", MEDIUM),
        Player("Axel Leszczynska", MEDIUM),
        Player("Bastian Kallin", HARD),
        Player("Benjamin Arulanantham", EASY),
        Player("Cesar Agerberg", MEDIUM),
        Player("Charlie Rodhelind", HARD),
        Player("Colin Isenia", MEDIUM),
        Player("Dexter Willis", MEDIUM),
        Player("Eivin Kallin", HARD),
        Player("Eliah Götesson", MEDIUM),
        Player("Ennio Mazza Klemi", HARD),
        Player("Filip Hagenblad", MEDIUM),
        Player("Filip Jönsson", HARD),
        Player("Gabriel Berglund", EASY),
        Player("Henning Brink", EASY),
        Player("Henning Ulnes", HARD),
        Player("Ivan Tegnander Weinreich", EASY),
        Player("Johannes Roginski", EASY),
        Player("John Måhlén", MEDIUM),
        Player("Kusai Hamidi", MEDIUM),
        Player("Lee Kindbom", HARD),
        Player("Leo Björling", MEDIUM),
        Player("Mattis Morland Soläng", MEDIUM),
        Player("Moahamd Hussein", EASY),
        Player("Orlando Nenchu", MEDIUM),
        Player("Oskar ege Findik", HARD),
        Player("Otto Eidevall", EASY),
        Player("Ruben Hernandez", MEDIUM),
        Player("Samuel Rådberg", MEDIUM),
        Player("Talha Haider", EASY),
        Player("Yousef Shashit", EASY)
    )
} 