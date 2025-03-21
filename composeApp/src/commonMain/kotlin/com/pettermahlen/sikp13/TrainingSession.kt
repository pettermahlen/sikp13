package com.pettermahlen.sikp13

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.ExperimentalMaterialApi

enum class AttendanceStatus {
    PRESENT,
    NOT_PRESENT,
    UNKNOWN;

    fun displayName(): String = when (this) {
        PRESENT -> "Ja"
        NOT_PRESENT -> "Nej"
        UNKNOWN -> "?"
    }
}

enum class GroupingMode {
    EVEN,
    SKILL_BASED;

    fun displayName(): String = when (this) {
        EVEN -> "Jämna"
        SKILL_BASED -> "Nivåer"
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
        
        // Calculate base size and how many groups need an extra player
        val baseSize = players.size / groupCount
        val extraPlayers = players.size % groupCount
        
        // Create groups with correct sizes
        val groups = mutableListOf<MutableList<Player>>()
        var currentIndex = 0
        
        for (i in 0 until groupCount) {
            val groupSize = baseSize + if (i < extraPlayers) 1 else 0
            groups.add(sortedPlayers.subList(currentIndex, currentIndex + groupSize).toMutableList())
            currentIndex += groupSize
        }
        
        return groups
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AttendanceChips(
    currentStatus: AttendanceStatus,
    onStatusChanged: (AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier,
    availableStatuses: List<AttendanceStatus> = AttendanceStatus.values().toList()
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        availableStatuses.forEach { status ->
            FilterChip(
                selected = currentStatus == status,
                onClick = { onStatusChanged(status) },
                modifier = Modifier.weight(1f),
                colors = ChipDefaults.filterChipColors(
                    selectedBackgroundColor = MaterialTheme.colors.primary,
                    selectedContentColor = MaterialTheme.colors.onPrimary,
                ),
            ) {
                Text(
                    text = status.displayName(),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun GroupCountSelector(
    selectedGroupCount: Int,
    isDropDownExpanded: Boolean,
    onDropDownExpandedChange: (Boolean) -> Unit,
    onGroupCountSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { onDropDownExpandedChange(true) }
        ) {
            Text("#Grupper: $selectedGroupCount")
        }
        
        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = { onDropDownExpandedChange(false) }
        ) {
            (2..8).forEach { count ->
                DropdownMenuItem(
                    onClick = {
                        onGroupCountSelected(count)
                        onDropDownExpandedChange(false)
                    }
                ) {
                    Text("$count")
                }
            }
        }
    }
}

@Composable
private fun GroupModeSelector(
    groupingMode: GroupingMode,
    onGroupingModeChanged: (GroupingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { 
            onGroupingModeChanged(
                if (groupingMode == GroupingMode.EVEN) 
                    GroupingMode.SKILL_BASED 
                else GroupingMode.EVEN
            )
        },
        modifier = modifier
    ) {
        Text(groupingMode.displayName())
    }
}

@Composable
private fun GroupControls(
    selectedGroupCount: Int,
    isDropDownExpanded: Boolean,
    onDropDownExpandedChange: (Boolean) -> Unit,
    onGroupCountSelected: (Int) -> Unit,
    groupingMode: GroupingMode,
    onGroupingModeChanged: (GroupingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupCountSelector(
                selectedGroupCount = selectedGroupCount,
                isDropDownExpanded = isDropDownExpanded,
                onDropDownExpandedChange = onDropDownExpandedChange,
                onGroupCountSelected = onGroupCountSelected
            )
            
            GroupModeSelector(
                groupingMode = groupingMode,
                onGroupingModeChanged = onGroupingModeChanged
            )
        }
    }
}

@Composable
private fun PlayerGroup(
    players: List<Player>,
    allPlayers: List<Player>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            players.forEach { player ->
                Text(
                    formatPlayerName(player, allPlayers),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun TrainingGroups(
    groups: List<List<Player>>,
    allPlayers: List<Player>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(groups.chunked(2)) { groupPair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupPair.forEach { group ->
                    PlayerGroup(
                        players = group,
                        allPlayers = allPlayers,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (groupPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PlayerAttendanceRow(
    player: Player,
    currentStatus: AttendanceStatus,
    onStatusChanged: (AttendanceStatus) -> Unit,
    availableStatuses: List<AttendanceStatus>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = player.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )
        AttendanceChips(
            currentStatus = currentStatus,
            onStatusChanged = onStatusChanged,
            modifier = Modifier.weight(1f),
            availableStatuses = availableStatuses
        )
    }
}

@Composable
private fun AttendanceSection(
    title: String,
    players: List<Player>,
    playerAttendance: Map<Player, AttendanceStatus>,
    onAttendanceChanged: (Player, AttendanceStatus) -> Unit,
    availableStatuses: List<AttendanceStatus>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(players) { player ->
                PlayerAttendanceRow(
                    player = player,
                    currentStatus = playerAttendance[player] ?: AttendanceStatus.UNKNOWN,
                    onStatusChanged = { status -> onAttendanceChanged(player, status) },
                    availableStatuses = availableStatuses
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrainingSession(
    onNavigateBack: () -> Unit,
    players: List<Player>
) {
    val playerAttendance = remember { mutableStateOf(players.associateWith { AttendanceStatus.UNKNOWN }.toMutableMap()) }
    var selectedGroupCount by remember { mutableStateOf(3) }
    var groupingMode by remember { mutableStateOf(GroupingMode.EVEN) }
    var isDropDownExpanded by remember { mutableStateOf(false) }

    val presentPlayers = remember(playerAttendance.value) {
        playerAttendance.value.entries
            .filter { it.value == AttendanceStatus.PRESENT }
            .map { it.key }
    }

    val allPlayers = players

    val groups = remember(selectedGroupCount, groupingMode, presentPlayers) {
        derivedStateOf { 
            presentPlayers.divideIntoGroups(selectedGroupCount, groupingMode)
        }
    }

    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Träning") },
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
                GroupControls(
                    selectedGroupCount = selectedGroupCount,
                    isDropDownExpanded = isDropDownExpanded,
                    onDropDownExpandedChange = { isDropDownExpanded = it },
                    onGroupCountSelected = { selectedGroupCount = it },
                    groupingMode = groupingMode,
                    onGroupingModeChanged = { groupingMode = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    if (presentPlayers.isEmpty()) {
                        Text(
                            "Inga spelare närvarande",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                "Total players: ${presentPlayers.size}",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            TrainingGroups(
                                groups = groups.value,
                                allPlayers = allPlayers,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
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
                // Pending attendance section
                AttendanceSection(
                    title = "Närvarande? (${playerAttendance.value.count { it.value == AttendanceStatus.UNKNOWN }})",
                    players = allPlayers.filter { player -> 
                        playerAttendance.value[player] != AttendanceStatus.PRESENT &&
                        playerAttendance.value[player] != AttendanceStatus.NOT_PRESENT
                    },
                    playerAttendance = playerAttendance.value,
                    onAttendanceChanged = { player, status ->
                        playerAttendance.value = playerAttendance.value.toMutableMap().apply {
                            put(player, status)
                        }
                    },
                    availableStatuses = listOf(AttendanceStatus.PRESENT, AttendanceStatus.NOT_PRESENT),
                    modifier = Modifier.weight(0.6f)
                )

                Divider()

                // Absent players section
                AttendanceSection(
                    title = "Frånvarande (${playerAttendance.value.count { it.value == AttendanceStatus.NOT_PRESENT }})",
                    players = allPlayers.filter { player -> 
                        playerAttendance.value[player] == AttendanceStatus.NOT_PRESENT
                    },
                    playerAttendance = playerAttendance.value,
                    onAttendanceChanged = { player, status ->
                        playerAttendance.value = playerAttendance.value.toMutableMap().apply {
                            put(player, status)
                        }
                    },
                    availableStatuses = listOf(AttendanceStatus.PRESENT, AttendanceStatus.NOT_PRESENT),
                    modifier = Modifier.weight(0.4f)
                )
            }
        }
    }
} 