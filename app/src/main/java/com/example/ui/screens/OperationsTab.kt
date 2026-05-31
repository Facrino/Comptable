package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Accompaniment
import com.example.data.Transaction
import com.example.data.PlanComptableGeneral
import com.example.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsTab(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTxForEdit by remember { mutableStateOf<Transaction?>(null) }
    var txToDelete by remember { mutableStateOf<Transaction?>(null) }

    // Filter transactions to show only current months
    val monthlyTransactions = remember(transactions, selectedMonth) {
        transactions.filter { it.date.startsWith(selectedMonth) }
    }

    val slateBlue = Color(0xFF1D1B20)
    val accentIndigo = Color(0xFF6750A4)
    val emeraldGreen = Color(0xFF0F9D58)

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userFaculte by viewModel.userFaculte.collectAsStateWithLifecycle()
    val userParcours by viewModel.userParcours.collectAsStateWithLifecycle()
    val userNiveau by viewModel.userNiveau.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ALWAYS visible Profile Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Comptabilité de $userName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (userFaculte.isNotEmpty() || userParcours.isNotEmpty() || userNiveau.isNotEmpty()) {
                    Text(
                        text = "Étudiant à : $userFaculte\nParcours : $userParcours\nNiveau d'études : $userNiveau",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )
                } else {
                    Text(
                        text = "Aucune information universitaire configurée. Appuyez sur votre profil en haut à droite pour ajouter vos données.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
        if (monthlyTransactions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = "Aucune écriture",
                    tint = slateBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aucune écriture enregistrée pour ce mois",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = slateBlue.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Cliquez sur le bouton '+' pour ajouter votre première opération journalière.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = slateBlue.copy(alpha = 0.4f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp) // space for FAB
            ) {
                item {
                    Text(
                        text = "Journal des écritures d'origine (${monthlyTransactions.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = slateBlue.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(monthlyTransactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        onEditClick = { selectedTxForEdit = tx },
                        onDeleteClick = { txToDelete = tx }
                    )
                }
            }
        }

        // Add Floating Action Button (FAB)
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = accentIndigo,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .testTag("add_operation_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter écriture")
        }

        // Add Operation Dialog
        if (showAddDialog) {
            AddEditOperationDialog(
                selectedMonth = selectedMonth,
                onDismiss = { showAddDialog = false },
                onSave = { date, libelle, debAcc, debAmt, credAcc, credAmt, accArr ->
                    viewModel.addTransaction(date, libelle, debAcc, debAmt, credAcc, credAmt, accArr)
                    showAddDialog = false
                }
            )
        }

        // Edit Operation Dialog
        selectedTxForEdit?.let { currentTx ->
            AddEditOperationDialog(
                selectedMonth = selectedMonth,
                existingTx = currentTx,
                onDismiss = { selectedTxForEdit = null },
                onSave = { date, libelle, debAcc, debAmt, credAcc, credAmt, accArr ->
                    viewModel.editTransaction(currentTx.id, date, libelle, debAcc, debAmt, credAcc, credAmt, accArr)
                    selectedTxForEdit = null
                }
            )
        }

        // Delete Confirmation Dialog
        txToDelete?.let { currentTx ->
            AlertDialog(
                onDismissRequest = { txToDelete = null },
                title = { Text("Supprimer l'écriture ?") },
                text = {
                    Text("Êtes-vous sûr de vouloir supprimer définitivement l'écriture : '${currentTx.libelle}' de ${String.format("%.2f", currentTx.debitAmount)} Ar ?")
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            viewModel.deleteTransaction(currentTx)
                            txToDelete = null
                        }
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { txToDelete = null }) {
                        Text("Annuler")
                    }
                },
                modifier = Modifier.testTag("delete_confirm_dialog")
            )
        }
    }
}
}

@Composable
fun TransactionItemRow(
    transaction: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val primaryText = Color(0xFF1E293B)
    val secondaryText = Color(0xFF64748B)
    val customAccent = Color(0xFF6366F1)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: Date & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Date",
                        tint = customAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = transaction.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = secondaryText
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_button_${transaction.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = customAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_button_${transaction.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Row 2: Libellé (Title)
            Text(
                text = transaction.libelle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            HorizontalDivider(
                color = Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Double Entry table display
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Primary Debit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF), // soft blue
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "D",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF3B82F6),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Compte ${transaction.debitAccount}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = primaryText
                            )
                        )
                    }
                    Text(
                        text = "${String.format("%,.2f", transaction.debitAmount)} Ar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2563EB)
                        )
                    )
                }

                // Primary Credit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFECFDF5), // soft emerald
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "C",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Compte ${transaction.creditAccount}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = primaryText
                            )
                        )
                    }
                    Text(
                        text = "${String.format("%,.2f", transaction.creditAmount)} Ar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF059669)
                        )
                    )
                }

                // Accompaniments listing (VAT, transport, other)
                val accompaniments = remember(transaction) { transaction.getAccompaniments() }
                if (accompaniments.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Lignes d'accompagnement :",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryText
                                )
                            )
                            for (acc in accompaniments) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (acc.isDebit) "Débit" else "Crédit",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (acc.isDebit) Color(0xFF2563EB) else Color(0xFF059669),
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.width(42.dp)
                                        )
                                        Text(
                                            text = "Cpt ${acc.account} - ${acc.label}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = primaryText.copy(alpha = 0.8f),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }
                                    Text(
                                        text = "${String.format("%,.2f", acc.amount)} Ar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                            color = primaryText
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOperationDialog(
    selectedMonth: String,
    existingTx: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (date: String, libelle: String, debAcc: String, debAmt: Double, credAcc: String, credAmt: Double, accs: List<Accompaniment>) -> Unit
) {
    var date by remember { mutableStateOf(existingTx?.date ?: "$selectedMonth-20") }
    var libelle by remember { mutableStateOf(existingTx?.libelle ?: "") }
    var debitAccount by remember { mutableStateOf(existingTx?.debitAccount ?: "") }
    var debitAmountStr by remember { mutableStateOf(existingTx?.debitAmount?.toString() ?: "") }
    var creditAccount by remember { mutableStateOf(existingTx?.creditAccount ?: "") }
    var creditAmountStr by remember { mutableStateOf(existingTx?.creditAmount?.toString() ?: "") }

    // Accompaniment list state
    val rawAccompaniments = remember { mutableStateListOf<Accompaniment>() }
    
    // Form Inputs for a single accompaniment candidate
    var accLabel by remember { mutableStateOf("") }
    var accIsDebit by remember { mutableStateOf(true) }
    var accAccount by remember { mutableStateOf("") }
    var accAmountStr by remember { mutableStateOf("") }

    // Errors
    var errorMsg by remember { mutableStateOf("") }

    // Hydrate existing accompaniments if editing
    LaunchedEffect(existingTx) {
        if (existingTx != null) {
            rawAccompaniments.clear()
            rawAccompaniments.addAll(existingTx.getAccompaniments())
        }
    }

    // Calculations of journal equilibrium
    val baseDebit = debitAmountStr.toDoubleOrNull() ?: 0.0
    val baseCredit = creditAmountStr.toDoubleOrNull() ?: 0.0
    val totalDebit = baseDebit + rawAccompaniments.filter { it.isDebit }.sumOf { it.amount }
    val totalCredit = baseCredit + rawAccompaniments.filter { !it.isDebit }.sumOf { it.amount }
    val diff = totalDebit - totalCredit
    val isBalanced = Math.abs(diff) < 0.01

    val slateBlue = Color(0xFF1E293B)
    val accentIndigo = Color(0xFF6366F1)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingTx == null) "Nouvelle écriture" else "Modifier l'écriture",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = slateBlue)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMsg.isNotBlank()) {
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // General: Date & Libelle
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (AAAA-MM-JJ)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentIndigo, focusedLabelColor = accentIndigo),
                    modifier = Modifier.fillMaxWidth().testTag("op_field_date")
                )

                OutlinedTextField(
                    value = libelle,
                    onValueChange = { libelle = it },
                    label = { Text("Libellé de l'opération") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentIndigo, focusedLabelColor = accentIndigo),
                    modifier = Modifier.fillMaxWidth().testTag("op_field_libelle")
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                // Debit Header & Fields
                Text("Contrapartie Débit principal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2563EB)))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PcgAccountInputField(
                        value = debitAccount,
                        onValueChange = { debitAccount = it },
                        label = "N° de compte",
                        modifier = Modifier.weight(1f),
                        testTag = "op_field_debit_acc"
                    )
                    OutlinedTextField(
                        value = debitAmountStr,
                        onValueChange = { 
                            debitAmountStr = it
                            if (creditAmountStr.isEmpty() && rawAccompaniments.isEmpty()) {
                                creditAmountStr = it // auto-balance assistance!
                            }
                        },
                        label = { Text("Montant (Ar)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f).testTag("op_field_debit_amt")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Credit Header & Fields
                Text("Contrapartie Crédit principal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF059669)))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PcgAccountInputField(
                        value = creditAccount,
                        onValueChange = { creditAccount = it },
                        label = "N° de compte",
                        modifier = Modifier.weight(1f),
                        testTag = "op_field_credit_acc"
                    )
                    OutlinedTextField(
                        value = creditAmountStr,
                        onValueChange = { creditAmountStr = it },
                        label = { Text("Montant (Ar)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f).testTag("op_field_credit_amt")
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                // Accompaniments Addition Area
                Text(
                    text = "Lignes d'accompagnement additionnelles (Ex : TVA, transport, etc.)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                )

                // Accompaniment Candidate fields
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = accLabel,
                            onValueChange = { accLabel = it },
                            label = { Text("Libellé ligne (ex: TVA 20%)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_acc_label")
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sens :", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(end = 4.dp))
                            
                            // Debit Selection
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = accIsDebit,
                                    onClick = { accIsDebit = true },
                                    modifier = Modifier.testTag("add_acc_radio_debit")
                                )
                                Text("Débit", style = MaterialTheme.typography.bodySmall)
                            }
                            // Credit Selection
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = !accIsDebit,
                                    onClick = { accIsDebit = false },
                                    modifier = Modifier.testTag("add_acc_radio_credit")
                                )
                                Text("Crédit", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PcgAccountInputField(
                                value = accAccount,
                                onValueChange = { accAccount = it },
                                label = "N° de compte",
                                modifier = Modifier.weight(1f),
                                testTag = "add_acc_account"
                            )
                            OutlinedTextField(
                                value = accAmountStr,
                                onValueChange = { accAmountStr = it },
                                label = { Text("Montant (Ar)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1.2f).testTag("add_acc_amount")
                            )
                        }

                        Button(
                            onClick = {
                                val amt = accAmountStr.toDoubleOrNull() ?: 0.0
                                if (accLabel.isBlank() || accAccount.isBlank() || amt <= 0.0) {
                                    errorMsg = "Veuillez remplir correctement la ligne d'accompagnement (libellé, compte et montant > 0)"
                                } else {
                                    rawAccompaniments.add(
                                        Accompaniment(
                                            label = accLabel.trim(),
                                            isDebit = accIsDebit,
                                            account = accAccount.trim(),
                                            amount = amt
                                        )
                                    )
                                    // Reset fields
                                    accLabel = ""
                                    accAccount = ""
                                    accAmountStr = ""
                                    errorMsg = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = slateBlue),
                            modifier = Modifier.fillMaxWidth().testTag("add_acc_confirm_button")
                        ) {
                            Text("Confirmer et ajouter la ligne", fontSize = 12.sp)
                        }
                    }
                }

                // Render current added accompaniments
                if (rawAccompaniments.isNotEmpty()) {
                    rawAccompaniments.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.isDebit) "ID" else "IC",
                                    color = if (item.isDebit) Color(0xFF2563EB) else Color(0xFF059669),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "Cpt ${item.account} - ${item.label} : ${item.amount} Ar",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { rawAccompaniments.removeAt(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Retirer", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                // Journal Equilibirum Indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBalanced) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Equilibre comptable :",
                                style = MaterialTheme.typography.labelSmall.copy(color = slateBlue)
                            )
                            Text(
                                                                text = "Débits: ${String.format("%.2f", totalDebit)} Ar | Crédits: ${String.format("%.2f", totalCredit)} Ar",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                            )
                        }

                        if (isBalanced) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Equilibré", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Équilibré", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF047857), fontWeight = FontWeight.Bold))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = "Déséquilibre", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Écart", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold))
                                }
                                Text(
                                    text = "${String.format("%.2f", Math.abs(diff))} Ar",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = accentIndigo),
                onClick = {
                    val debAmt = debitAmountStr.toDoubleOrNull() ?: 0.0
                    val credAmt = creditAmountStr.toDoubleOrNull() ?: 0.0

                    if (date.isBlank() || libelle.isBlank() || debitAccount.isBlank() || creditAccount.isBlank() || debAmt <= 0.0 || credAmt <= 0.0) {
                        errorMsg = "Veuillez remplir tous les champs obligatoires avec des montants valides."
                    } else if (!isBalanced) {
                        errorMsg = "Attention : L'écriture n'est pas équilibrée (Total Débit doit être égal au Total Crédit) !"
                    } else {
                        onSave(
                            date.trim(),
                            libelle.trim(),
                            debitAccount.trim(),
                            debAmt,
                            creditAccount.trim(),
                            credAmt,
                            rawAccompaniments.toList()
                        )
                    }
                },
                modifier = Modifier.testTag("save_transaction_button")
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        modifier = Modifier.fillMaxWidth().testTag("add_edit_transaction_dialog")
    )
}

@Composable
fun PcgAccountInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }
    val exactMatch = remember(value) {
        PlanComptableGeneral.accounts.firstOrNull { it.code == value.trim() }
    }
    val suggestions = remember(value, isFocused) {
        if (!isFocused) emptyList()
        else if (value.isBlank()) {
            PlanComptableGeneral.accounts.take(4)
        } else {
            PlanComptableGeneral.search(value).take(4)
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6750A4),
                focusedLabelColor = Color(0xFF6750A4)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
                .testTag(testTag)
        )

        // Show the elegant actual account title when there is a complete exact match
        if (exactMatch != null) {
            Text(
                text = "✓ ${exactMatch.name}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF0F9D58),
                modifier = Modifier.padding(start = 6.dp, top = 2.dp)
            )
        }

        // Suggestions block (only visible when focused and there is no complete exact match, or if suggestions exist)
        if (isFocused && exactMatch == null && suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(
                        text = "Plan Comptable Général (PCG) :",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = Color(0xFF6750A4),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    
                    suggestions.forEach { acc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    onValueChange(acc.code)
                                }
                                .padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = acc.code,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF6750A4),
                                modifier = Modifier.width(44.dp)
                            )
                            Text(
                                text = acc.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFF1D1B20)
                            )
                        }
                    }
                }
            }
        }
    }
}
