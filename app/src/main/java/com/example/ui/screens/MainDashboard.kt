package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userFaculte by viewModel.userFaculte.collectAsStateWithLifecycle()
    val userParcours by viewModel.userParcours.collectAsStateWithLifecycle()
    val userNiveau by viewModel.userNiveau.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    var showProfileSettingsDialog by remember { mutableStateOf(false) }

    val cleanBackground = Color(0xFFFDF7FF)
    val cleanOnBackground = Color(0xFF1D1B20)
    val cleanPrimary = Color(0xFF6750A4)
    val cleanPrimaryContainer = Color(0xFFEADDFF)
    val cleanOnPrimaryContainer = Color(0xFF21005D)
    val supportingText = Color(0xFF49454F)
    val cleanNavBg = Color(0xFFF3EDF7)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val headerText = if (activeTab == 0) {
                            "Comptabilité de $userName"
                        } else {
                            "Comptabilité ZAINA"
                        }
                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = cleanOnBackground
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subText = if (activeTab == 0) {
                            if (userFaculte.isNotEmpty() || userParcours.isNotEmpty() || userNiveau.isNotEmpty()) {
                                "étudiant à $userFaculte, parcours $userParcours en $userNiveau"
                            } else {
                                "Complétez votre profil d'étudiant"
                            }
                        } else {
                            "Connecté: $userEmail"
                        }
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = supportingText,
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    // Profile/Logout Action - Styled like the header bar avatar block in the design mockup
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cleanPrimaryContainer)
                            .clickable { showProfileSettingsDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(cleanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = cleanOnPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Profil",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = cleanOnPrimaryContainer.copy(alpha = 0.7f),
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cleanBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = cleanNavBg,
                tonalElevation = 0.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Journal") },
                    label = { Text("Écritures", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cleanOnPrimaryContainer,
                        selectedTextColor = cleanOnPrimaryContainer,
                        indicatorColor = cleanPrimaryContainer,
                        unselectedIconColor = supportingText,
                        unselectedTextColor = supportingText
                    ),
                    modifier = Modifier.testTag("nav_tab_entries")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Résultat") },
                    label = { Text("Résultat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cleanOnPrimaryContainer,
                        selectedTextColor = cleanOnPrimaryContainer,
                        indicatorColor = cleanPrimaryContainer,
                        unselectedIconColor = supportingText,
                        unselectedTextColor = supportingText
                    ),
                    modifier = Modifier.testTag("nav_tab_result")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Bilan") },
                    label = { Text("Bilan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cleanOnPrimaryContainer,
                        selectedTextColor = cleanOnPrimaryContainer,
                        indicatorColor = cleanPrimaryContainer,
                        unselectedIconColor = supportingText,
                        unselectedTextColor = supportingText
                    ),
                    modifier = Modifier.testTag("nav_tab_bilan")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Send, contentDescription = "Sauvegarde") },
                    label = { Text("Sauvegarde", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cleanOnPrimaryContainer,
                        selectedTextColor = cleanOnPrimaryContainer,
                        indicatorColor = cleanPrimaryContainer,
                        unselectedIconColor = supportingText,
                        unselectedTextColor = supportingText
                    ),
                    modifier = Modifier.testTag("nav_tab_backup")
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Restauration") },
                    label = { Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cleanOnPrimaryContainer,
                        selectedTextColor = cleanOnPrimaryContainer,
                        indicatorColor = cleanPrimaryContainer,
                        unselectedIconColor = supportingText,
                        unselectedTextColor = supportingText
                    ),
                    modifier = Modifier.testTag("nav_tab_restore")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(cleanBackground)
        ) {
            // Month Selector Bar (Global Filter)
            MonthPeriodSelector(
                currentMonthStr = selectedMonth,
                onMonthChanged = { viewModel.changeMonth(it) },
                accentColor = cleanPrimary
            )

            // Active Tab Content Renderer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> OperationsTab(viewModel)
                    1 -> CompteResultatTab(viewModel)
                    2 -> BilanTab(viewModel)
                    3 -> BackupRestoreTab(viewModel, showBackupState = true)
                    4 -> BackupRestoreTab(viewModel, showBackupState = false)
                }
            }
        }
    }

    if (showProfileSettingsDialog) {
        Dialog(
            onDismissRequest = { showProfileSettingsDialog = false }
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header icon and title
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(cleanPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Éducation",
                            tint = cleanPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Profil de l'Étudiant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = cleanOnBackground
                        )
                    )

                    Text(
                        text = "Renseignez vos informations universitaires pour personnaliser vos rapports professionnels.",
                        style = MaterialTheme.typography.bodySmall,
                        color = supportingText,
                        textAlign = TextAlign.Center
                    )

                    var tempName by remember { mutableStateOf(userName) }
                    var tempFaculte by remember { mutableStateOf(userFaculte) }
                    var tempParcours by remember { mutableStateOf(userParcours) }
                    var tempNiveau by remember { mutableStateOf(userNiveau) }

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nom complet") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tempFaculte,
                        onValueChange = { tempFaculte = it },
                        label = { Text("Établissement / Faculté") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ex: Faculté des Sciences") }
                    )

                    OutlinedTextField(
                        value = tempParcours,
                        onValueChange = { tempParcours = it },
                        label = { Text("Parcours / Filière") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ex: Sciences Économiques") }
                    )

                    OutlinedTextField(
                        value = tempNiveau,
                        onValueChange = { tempNiveau = it },
                        label = { Text("Niveau d'études") },
                        leadingIcon = { Icon(Icons.Default.Grade, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ex: Licence 3") }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons
                    Button(
                        onClick = {
                            viewModel.updateProfile(tempName, tempFaculte, tempParcours, tempNiveau)
                            showProfileSettingsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cleanPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enregistrer les données", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = {
                            showProfileSettingsDialog = false
                            viewModel.logout()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Se Déconnecter", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Utility to switch Month Periods
@Composable
fun MonthPeriodSelector(
    currentMonthStr: String,
    onMonthChanged: (String) -> Unit,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onMonthChanged(getPreviousMonth(currentMonthStr))
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Mois précédent",
                    tint = accentColor
                )
            }

            Text(
                text = "${formatYearMonthFrench(currentMonthStr)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )

            IconButton(onClick = {
                onMonthChanged(getNextMonth(currentMonthStr))
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Mois suivant",
                    tint = accentColor
                )
            }
        }
    }
}

// Logic to shift YYYY-MM dates
fun getPreviousMonth(current: String): String {
    val parts = current.split("-")
    if (parts.size < 2) return current
    var year = parts[0].toIntOrNull() ?: 2026
    var month = parts[1].toIntOrNull() ?: 5
    month--
    if (month < 1) {
        month = 12
        year--
    }
    return String.format("%04d-%02d", year, month)
}

fun getNextMonth(current: String): String {
    val parts = current.split("-")
    if (parts.size < 2) return current
    var year = parts[0].toIntOrNull() ?: 2026
    var month = parts[1].toIntOrNull() ?: 5
    month++
    if (month > 12) {
        month = 1
        year++
    }
    return String.format("%04d-%02d", year, month)
}

fun formatYearMonthFrench(current: String): String {
    val parts = current.split("-")
    if (parts.size < 2) return current
    val year = parts[0]
    val monthNum = parts[1]
    val monthName = when (monthNum) {
        "01" -> "Janvier"
        "02" -> "Février"
        "03" -> "Mars"
        "04" -> "Avril"
        "05" -> "Mai"
        "06" -> "Juin"
        "07" -> "Juillet"
        "08" -> "Août"
        "09" -> "Septembre"
        "10" -> "Octobre"
        "11" -> "Novembre"
        "12" -> "Décembre"
        else -> monthNum
    }
    return "$monthName $year"
}
