package com.pettermahlen.sikp13

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

private sealed class Screen {
    data object PlayerList : Screen()
    data object TrainingSession : Screen()
}

@Composable
@Preview
fun App() {
    val playersDb: PlayersDb = HardwiredPlayersDb()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PlayerList) }
    
    MaterialTheme {
        when (currentScreen) {
            Screen.PlayerList -> PlayerListScreen(
                playersDb = playersDb,
                onStartTrainingSession = { currentScreen = Screen.TrainingSession }
            )
            Screen.TrainingSession -> TrainingSession(
                playersDb = playersDb,
                onNavigateBack = { currentScreen = Screen.PlayerList }
            )
        }
    }
}

@Composable
private fun PlayerListScreen(
    playersDb: PlayersDb,
    onStartTrainingSession: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Players",
                style = MaterialTheme.typography.h4
            )
            Button(
                onClick = onStartTrainingSession,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text("Start Training")
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
                items(playersDb.listPlayers()) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = player.skillLevel.name,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}