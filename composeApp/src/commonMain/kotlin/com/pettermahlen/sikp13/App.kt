package com.pettermahlen.sikp13

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

private sealed class Screen {
    data object StartScreen : Screen()
    data object PlayerList : Screen()
    data object TrainingSession : Screen()
}

@Composable
@Preview
fun App() {
    val playersDb: PlayersDb = DatabaseProvider.getPlayersDb()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.StartScreen) }
    var allPlayers by remember { mutableStateOf(playersDb.listPlayers()) }
    
    MaterialTheme {
        when (currentScreen) {
            Screen.StartScreen -> StartScreen(
                onStartTraining = { 
                    allPlayers = playersDb.listPlayers() // Refresh players before showing training session
                    currentScreen = Screen.TrainingSession 
                },
                onListPlayers = { currentScreen = Screen.PlayerList }
            )
            Screen.PlayerList -> PlayerListScreen(
                playersDb = playersDb,
                onStartTrainingSession = { 
                    allPlayers = playersDb.listPlayers() // Refresh players before showing training session
                    currentScreen = Screen.TrainingSession 
                },
                onPlayersChanged = { // Add callback for player changes
                    allPlayers = playersDb.listPlayers()
                }
            )
            Screen.TrainingSession -> TrainingSession(
                players = allPlayers,
                onNavigateBack = { currentScreen = Screen.StartScreen }
            )
        }
    }
}

@Composable
private fun StartScreen(
    onStartTraining: () -> Unit,
    onListPlayers: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Text(
            "SIK P13 Träningsapp",
            style = MaterialTheme.typography.h3,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
        ) {
            Text("Starta Träning")
        }
        
        Button(
            onClick = onListPlayers,
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
        ) {
            Text("Spelarlista")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayerListScreen(
    playersDb: PlayersDb,
    onStartTrainingSession: () -> Unit,
    onPlayersChanged: () -> Unit // Add callback parameter
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPlayerName by remember { mutableStateOf<Player?>(null) }
    var editingPlayerSkill by remember { mutableStateOf<Player?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Player?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var players by remember { mutableStateOf(playersDb.listPlayers()) }

    fun refreshPlayers() {
        players = playersDb.listPlayers()
        onPlayersChanged() // Notify parent about player changes
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ny Spelare")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxWidth().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Spelare",
                    style = MaterialTheme.typography.h4
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showResetConfirmation = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.error
                        )
                    ) {
                        Text("Återställ P13")
                    }
                    Button(
                        onClick = onStartTrainingSession
                    ) {
                        Text("Starta Träning")
                    }
                }
            }
            
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            ) {
                val state = rememberLazyListState()
                
                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(players) { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.clickable { editingPlayerName = player }
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(player.skillLevel.color.copy(alpha = 0.2f))
                                        .clickable { editingPlayerSkill = player }
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = player.skillLevel.displayName,
                                        style = MaterialTheme.typography.caption,
                                        color = player.skillLevel.color
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showDeleteConfirmation = player }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Ta bort")
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        PlayerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, skillLevel ->
                val player = Player(name, skillLevel)
                playersDb.insertPlayer(player)
                refreshPlayers()
            }
        )
    }

    // Edit Name Dialog
    if (editingPlayerName != null) {
        PlayerNameDialog(
            currentName = editingPlayerName!!.name,
            onDismiss = { editingPlayerName = null },
            onConfirm = { newName ->
                val updatedPlayer = editingPlayerName!!.copy(name = newName)
                playersDb.updatePlayer(updatedPlayer)
                refreshPlayers()
                editingPlayerName = null
            }
        )
    }

    // Edit Skill Level Dialog
    if (editingPlayerSkill != null) {
        SkillLevelDialog(
            currentSkillLevel = editingPlayerSkill!!.skillLevel,
            onDismiss = { editingPlayerSkill = null },
            onConfirm = { newSkillLevel ->
                val updatedPlayer = editingPlayerSkill!!.copy(skillLevel = newSkillLevel)
                playersDb.updatePlayer(updatedPlayer)
                refreshPlayers()
                editingPlayerSkill = null
            }
        )
    }

    // Delete Confirmation Dialog
    val playerToDelete = showDeleteConfirmation // Capture in local val to ensure it's not null during the operation
    if (playerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Ta bort spelare") },
            text = { Text("Ta bort ${playerToDelete.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Check if player still exists before deleting
                        if (playersDb.getPlayer(playerToDelete.name) != null) {
                            playersDb.deletePlayer(playerToDelete.name)
                            refreshPlayers()
                        }
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Ta bort")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = null }
                ) {
                    Text("Avbryt")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        var password by remember { mutableStateOf("") }
        var showError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Återställ spelare") },
            text = { 
                Column {
                    if ((playersDb as? SqlDelightPlayersDb)?.isEmpty() == true) {
                        Text("Vill du återställa spelarlistan till P13-listan?")
                    } else {
                        Text("OBS! Detta kommer att ta bort alla nuvarande spelare och lägga in spelare från grundlistan för P13.")
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            showError = false
                        },
                        label = { Text("Lösenord") },
                        isError = showError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                    if (showError) {
                        Text(
                            "Fel lösenord",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val sqlDb = playersDb as? SqlDelightPlayersDb
                            if (sqlDb != null) {
                                // First verify we can decrypt the player list with the given password
                                sqlDb.insertInitialPlayers(password)
                                // If we get here, the password was correct, now we can safely delete the old players
                                players.forEach { player ->
                                    sqlDb.deletePlayer(player.name)
                                }
                                showResetConfirmation = false
                                refreshPlayers()
                            } else {
                                showError = true
                            }
                        } catch (e: Exception) {
                            showError = true
                        }
                    }
                ) {
                    Text("Återställ")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmation = false }
                ) {
                    Text("Avbryt")
                }
            }
        )
    }
}