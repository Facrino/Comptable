package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreTab(
    viewModel: TransactionViewModel,
    showBackupState: Boolean,
    modifier: Modifier = Modifier
) {
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    var selectedBackupService by remember { mutableStateOf("Firebase Realtime Database") }
    var restoreInputJson by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val slateBlue = Color(0xFF1D1B20)
    val accentIndigo = Color(0xFF6750A4)
    val emeraldGreen = Color(0xFF0F9D58)

    // Automatically populate helper json if we are on restore tab and have a succesful backup
    var simulatedCloudBackupJson by remember { mutableStateOf("") }
    LaunchedEffect(syncStatus) {
        if (syncStatus is TransactionViewModel.SyncState.Success) {
            val lastJson = (syncStatus as TransactionViewModel.SyncState.Success).lastBackupJson
            if (lastJson.isNotBlank()) {
                simulatedCloudBackupJson = lastJson
                if (restoreInputJson.isBlank()) {
                    restoreInputJson = lastJson
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and explanation header card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (showBackupState) accentIndigo.copy(alpha = 0.1f) else emeraldGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (showBackupState) Icons.Default.CloudUpload else Icons.Default.CloudDownload,
                        contentDescription = "Sync Header",
                        tint = if (showBackupState) accentIndigo else emeraldGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (showBackupState) "Sauvegarde Cloud" else "Restauration Cloud",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                    )
                    Text(
                        text = "Connecté en tant que $userEmail",
                        style = MaterialTheme.typography.labelSmall.copy(color = slateBlue.copy(alpha = 0.5f))
                    )
                }
            }
        }

        if (showBackupState) {
            // Exclusive Firebase Backup interface
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sauvegarde sur base de données Cloud :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                    )

                    Surface(
                        color = Color(0xFFF7F2FA),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Firebase Active",
                                tint = accentIndigo,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Sauvegarde Cloud Sécurisée",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                                )
                                Text(
                                    text = "Sauvegarde synchrone professionnelle, immédiate et hautement sécurisée.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = slateBlue.copy(alpha = 0.7f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Écritures prêtes pour la synchronisation : ${allTransactions.size} transactions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = slateBlue.copy(alpha = 0.8f)
                    )

                    Button(
                        onClick = { viewModel.backupToCloud() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("backup_execute_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Upload")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sauvegarder sur votre espace Cloud sécurisé", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Restore Tab
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Récupérer la comptabilité depuis le cloud d'origine :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                    )

                    // Helper indicator showing if a cloud backup exists
                    if (simulatedCloudBackupJson.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = "Backup found", tint = accentIndigo, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sauvegarde en cache Cloud sécurisé détectée ! Prête à être restaurée.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1E40AF), fontWeight = FontWeight.SemiBold)
                            )
                        }
                    } else {
                        Text(
                            text = "Note: Si vous n'avez pas encore fait de sauvegarde dans l'onglet précédent durant cette session, vous pouvez également coller n'importe quel fichier JSON valide ci-dessous pour forcer la restauration.",
                            style = MaterialTheme.typography.bodySmall.copy(color = slateBlue.copy(alpha = 0.6f))
                        )
                    }

                    OutlinedTextField(
                        value = restoreInputJson,
                        onValueChange = { restoreInputJson = it },
                        label = { Text("Contenu de la sauvegarde (JSON)") },
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("restore_input_json")
                    )

                    Button(
                        onClick = { viewModel.restoreFromCloud(restoreInputJson) },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("restore_execute_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Download")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restaurer la comptabilité", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Loader & Status Logs card
        AnimatedVisibility(
            visible = syncStatus != TransactionViewModel.SyncState.Idle,
            enter = fadeIn() + expandVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("sync_status_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Journal des liaisons cloud",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                        )
                        IconButton(onClick = { viewModel.clearSyncStatus() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close status", tint = slateBlue.copy(alpha = 0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (val state = syncStatus) {
                        is TransactionViewModel.SyncState.Syncing -> {
                            CircularProgressIndicator(color = accentIndigo, modifier = Modifier.size(45.dp).testTag("sync_progress"))
                            Text(
                                text = "Liaison au serveur authentifiée. Chiffrement AES-256 en cours...",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = slateBlue.copy(alpha = 0.8f)
                            )
                        }

                        is TransactionViewModel.SyncState.Success -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = emeraldGreen, modifier = Modifier.size(48.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF047857),
                                textAlign = TextAlign.Center
                            )

                            // If we have back up text, show selection container so user can copy it manually if they wish
                            if (state.lastBackupJson.isNotBlank()) {
                                Text(
                                    text = "Copie de sauvegarde locale de sécurité (JSON chiffré) :",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = slateBlue.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start
                                )

                                SelectionContainer {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = state.lastBackupJson,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            ),
                                            color = slateBlue
                                        )
                                    }
                                }
                            }
                        }

                        is TransactionViewModel.SyncState.Error -> {
                            Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
                            Text(
                                text = state.error,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
