package com.pettermahlen.sikp13

fun parseCSV(csvData: String): List<Player> {
  if (csvData.isEmpty()) return emptyList()

  val lines = csvData.lines().filter { it.isNotBlank() }
  if (lines.isEmpty()) return emptyList()

  // Skip header if present
  val dataLines =
      if (lines[0].lowercase().contains("name") && lines[0].lowercase().contains("skill")) {
        lines.drop(1)
      } else {
        lines
      }

  return dataLines.mapIndexed { index, line ->
    try {
      val fields = parseCSVLine(line)

      if (fields.size != 2) {
        throw IllegalArgumentException(
            "Invalid CSV format - each line must have name and skill level")
      }
      try {
        Player(fields[0], SkillLevel.valueOf(fields[1]))
      } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid skill level '${fields[1]}'")
      }
    } catch (e: IllegalArgumentException) {
      throw IllegalArgumentException(
          "Error on line ${index + 1}: ${e.message}\nLine content: $line", e)
    }
  }
}

private fun parseCSVLine(str: String): List<String> = str.split(",").map { it.trim() }

object PlayerListCSV {
  fun toCSV(players: List<Player>): String {
    val header = "name,skill_level"
    if (players.isEmpty()) return header

    return header +
        "\n" +
        players.joinToString("\n") { player ->
          "${escapeCSV(player.name)},${player.skillLevel.name}"
        }
  }

  private fun escapeCSV(str: String): String {
    return if (str.contains(",") || str.contains("\"")) {
      // Count quotes to ensure we don't have an odd number
      val quoteCount = str.count { it == '\"' }
      if (quoteCount % 2 != 0) {
        throw IllegalArgumentException("String contains an unterminated quote: $str")
      }
      "\"${str.replace("\"", "\"\"")}\""
    } else str
  }
}
