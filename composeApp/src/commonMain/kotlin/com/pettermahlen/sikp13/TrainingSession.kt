package com.pettermahlen.sikp13

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class AttendanceStatus {
    PRESENT,
    NOT_PRESENT,
    UNKNOWN;

    fun displayName(): String = when (this) {
        PRESENT -> "Present"
        NOT_PRESENT -> "Not Present"
        UNKNOWN -> "Unknown"
    }
}

enum class GroupingMode {
    EVEN,
    SKILL_BASED;

    fun displayName(): String = when (this) {
        EVEN -> "Jämna"
        SKILL_BASED -> "Nivåindelat"
    }
}

interface GroupDivider {
    fun divideIntoGroups(players: List<Player>, groupCount: Int): List<List<Player>>
}

class SkillBasedDivider : GroupDivider {
    override fun divideIntoGroups(players: List<Player>, groupCount: Int): List<List<Player>> {
        if (players.isEmpty()) return emptyList()
        if (groupCount <= 0) return listOf(players)

        // Sort players by skill level in descending order
        val sortedPlayers = players.sortedByDescending { it.skillLevel }
        
        // Calculate how many players should be in each group
        val playersPerGroup = (players.size + groupCount - 1) / groupCount
        
        // Create groups and distribute players
        return sortedPlayers.chunked(playersPerGroup)
            .take(groupCount) // Ensure we don't create more groups than requested
            .map { it.toMutableList() } // Convert to mutable list as required by return type
    }
}

class EvenDivider : GroupDivider {
    override fun divideIntoGroups(players: List<Player>, groupCount: Int): List<List<Player>> {
        if (players.isEmpty()) return emptyList()
        if (groupCount <= 0) return listOf(players)

        // Sort players by skill level in descending order
        val sortedPlayers = players.sortedByDescending { it.skillLevel }
        
        // Create empty groups
        val groups = List(groupCount) { mutableListOf<Player>() }
        
        // Use snake pattern to distribute players evenly
        // First half of players go forward, second half go backward
        sortedPlayers.forEachIndexed { index, player ->
            val groupIndex = if (index % (2 * groupCount) < groupCount) {
                index % groupCount
            } else {
                groupCount - 1 - (index % groupCount)
            }
            groups[groupIndex].add(player)
        }
        
        return groups
    }
}

private fun formatPlayerName(player: Player, allPlayers: List<Player>): String {
    val firstName = player.name.split(" ")[0]
    val playersWithSameFirstName = allPlayers.count { 
        it.name.split(" ")[0] == firstName 
    }
    
    return if (playersWithSameFirstName > 1) {
        "$firstName ${player.name.split(" ").last()[0]}"
    } else {
        firstName
    }
}

private fun List<Player>.divideIntoGroups(groupCount: Int, mode: GroupingMode): List<List<Player>> {
    val divider = when (mode) {
        GroupingMode.SKILL_BASED -> SkillBasedDivider()
        GroupingMode.EVEN -> EvenDivider()
    }
    return divider.divideIntoGroups(this, groupCount)
}

@Composable
fun TrainingSession(
    onNavigateBack: () -> Unit,
    playersDb: PlayersDb
) {
    val playerAttendance = remember { mutableStateOf(playersDb.listPlayers().associateWith { AttendanceStatus.UNKNOWN }.toMutableMap()) }
    var selectedGroupCount by remember { mutableStateOf(2) }
    var groupingMode by remember { mutableStateOf(GroupingMode.EVEN) }
    var isDropDownExpanded by remember { mutableStateOf(false) }

    val presentPlayers = remember(playerAttendance.value) {
        playerAttendance.value.entries
            .filter { it.value == AttendanceStatus.PRESENT }
            .map { it.key }
    }

    val groups = remember(selectedGroupCount, groupingMode, presentPlayers) {
        derivedStateOf { 
            presentPlayers.divideIntoGroups(selectedGroupCount, groupingMode)
        }
    }

    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Training Session") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
            
            // Training Groups Section - Top half
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Training Groups",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            OutlinedButton(
                                onClick = { isDropDownExpanded = true }
                            ) {
                                Text("#Grupper: $selectedGroupCount")
                            }
                            
                            DropdownMenu(
                                expanded = isDropDownExpanded,
                                onDismissRequest = { isDropDownExpanded = false }
                            ) {
                                (2..8).forEach { count ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedGroupCount = count
                                            isDropDownExpanded = false
                                        }
                                    ) {
                                        Text("$count")
                                    }
                                }
                            }
                        }
                        
                        Button(
                            onClick = { 
                                groupingMode = if (groupingMode == GroupingMode.EVEN) 
                                    GroupingMode.SKILL_BASED else GroupingMode.EVEN
                            }
                        ) {
                            Text(groupingMode.displayName())
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    if (presentPlayers.isEmpty()) {
                        Text(
                            "No players marked as present",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        val currentGroups = groups.value
                        
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Display total player count
                            Text(
                                "Total players: ${presentPlayers.size}",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Scrollable groups section
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(currentGroups.chunked(2)) { groupPair ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            groupPair.forEach { group ->
                                                Card(
                                                    modifier = Modifier.weight(1f),
                                                    elevation = 2.dp
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(8.dp)
                                                    ) {
                                                        Text(
                                                            "Group ${currentGroups.indexOf(group) + 1}",
                                                            style = MaterialTheme.typography.subtitle1
                                                        )
                                                        group.forEach { player ->
                                                            Text(
                                                                formatPlayerName(player, presentPlayers),
                                                                style = MaterialTheme.typography.body2,
                                                                modifier = Modifier.padding(vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            // If we have an odd number of groups in this row, add an empty weight
                                            if (groupPair.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Divider()

            // Attendance Section - Bottom half
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    "Mark Player Attendance",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val nonPresentPlayers = playersDb.listPlayers()
                        .filter { player -> playerAttendance.value[player] != AttendanceStatus.PRESENT }
                    
                    items(nonPresentPlayers) { player ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.body1
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AttendanceStatus.values().forEach { status ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = playerAttendance.value[player] == status,
                                            onClick = {
                                                playerAttendance.value = playerAttendance.value.toMutableMap().apply {
                                                    put(player, status)
                                                }
                                            }
                                        )
                                        Text(
                                            text = status.displayName(),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Summary of attendance
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val presentCount = playerAttendance.value.count { it.value == AttendanceStatus.PRESENT }
                    val notPresentCount = playerAttendance.value.count { it.value == AttendanceStatus.NOT_PRESENT }
                    val unknownCount = playerAttendance.value.count { it.value == AttendanceStatus.UNKNOWN }
                    
                    Text(
                        "Attendance Summary",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text("Present: $presentCount")
                    Text("Not Present: $notPresentCount")
                    Text("Unknown: $unknownCount")
                }
            }
        }
    }
} 