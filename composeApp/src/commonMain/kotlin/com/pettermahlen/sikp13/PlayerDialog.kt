package com.pettermahlen.sikp13

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SkillLevelDialog(
    currentSkillLevel: SkillLevel,
    onDismiss: () -> Unit,
    onConfirm: (SkillLevel) -> Unit
) {
    var selectedSkillLevel by remember { mutableStateOf(currentSkillLevel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ändra nivå") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkillLevel.values().forEach { skillLevel ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedSkillLevel == skillLevel,
                            onClick = { selectedSkillLevel = skillLevel }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            FilterChip(
                                selected = true,
                                onClick = { selectedSkillLevel = skillLevel },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ChipDefaults.filterChipColors(
                                    selectedBackgroundColor = skillLevel.color,
                                    selectedContentColor = if (skillLevel == SkillLevel.HARD) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(skillLevel.displayName)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedSkillLevel)
                    onDismiss()
                }
            ) {
                Text("Spara")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ändra namn") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showError) {
                    Text(
                        "Skriv ett namn",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        showError = false
                    },
                    label = { Text("Namn") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name)
                        onDismiss()
                    }
                }
            ) {
                Text("Spara")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerDialog(
    player: Player? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, SkillLevel) -> Unit
) {
    var name by remember { mutableStateOf(player?.name ?: "") }
    var selectedSkillLevel by remember { mutableStateOf(player?.skillLevel ?: SkillLevel.MEDIUM) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(if (player == null) "Ny Spelare" else "Ändra Spelare") 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showError) {
                    Text(
                        "Skriv ett namn",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        showError = false
                    },
                    label = { Text("Namn") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Nivå", style = MaterialTheme.typography.subtitle1)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkillLevel.values().forEach { skillLevel ->
                        FilterChip(
                            selected = selectedSkillLevel == skillLevel,
                            onClick = { selectedSkillLevel = skillLevel },
                            modifier = Modifier.weight(1f),
                            colors = ChipDefaults.filterChipColors(
                                selectedBackgroundColor = skillLevel.color,
                                selectedContentColor = if (skillLevel == SkillLevel.HARD) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary
                            )
                        ) {
                            Text(skillLevel.displayName)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name, selectedSkillLevel)
                        onDismiss()
                    }
                }
            ) {
                Text(if (player == null) "Lägg till" else "Spara")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
} 