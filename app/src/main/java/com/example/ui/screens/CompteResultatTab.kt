package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AccountDetail
import com.example.ui.viewmodel.TransactionViewModel

@Composable
fun CompteResultatTab(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.incomeStatement.collectAsStateWithLifecycle()

    val primaryBg = Color(0xFFFDF7FF)
    val slateBlue = Color(0xFF1D1B20)
    val accentIndigo = Color(0xFF6750A4)

    // Expandable cards states
    var expandCA by remember { mutableStateOf(false) }
    var expandConso by remember { mutableStateOf(false) }
    var expandEBE by remember { mutableStateOf(false) }
    var expandExploit by remember { mutableStateOf(false) }
    var expandFin by remember { mutableStateOf(false) }
    var expandExc by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(primaryBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = slateBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Résultat Net de l'Exercice",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    val netResult = report.resExploitationVal + report.resFinancierVal + report.resExceptionnelVal
                    Text(
                        text = "${String.format("%,.2f", netResult)} Ar",
                        color = if (netResult >= 0) Color(0xFF34D399) else Color(0xFFF87171),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = if (netResult >= 0) "Bénéfice net généré" else "Déficit net constaté",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text(
                text = "Structure d'exploitation mensuelle",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = slateBlue.copy(alpha = 0.8f)),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // 1. Chiffre d'Affaires (CA)
        item {
            FinancialReportingCard(
                title = "Chiffre d'Affaires (CA)",
                value = report.caVal,
                isRevenue = true,
                isExpanded = expandCA,
                onToggleExpand = { expandCA = !expandCA },
                detailsList = report.caDetails,
                testTag = "cr_card_ca"
            )
        }

        // 2. Consommation (Achats et services)
        item {
            FinancialReportingCard(
                title = "Consommation intermédiaires",
                value = report.consoVal,
                isRevenue = false,
                isExpanded = expandConso,
                onToggleExpand = { expandConso = !expandConso },
                detailsList = report.consoDetails,
                testTag = "cr_card_conso"
            )
        }

        // 3. Excédent Brut d'Exploitation (EBE)
        item {
            FinancialReportingCard(
                title = "Excédent Brut d'Exploitation (EBE)",
                value = report.ebeVal,
                isRevenue = report.ebeVal >= 0,
                isExpanded = expandEBE,
                onToggleExpand = { expandEBE = !expandEBE },
                detailsList = report.ebeDetails,
                subText = "CA - Consommation - Personnel - Taxes",
                testTag = "cr_card_ebe"
            )
        }

        // 4. Résultat d'Exploitation
        item {
            FinancialReportingCard(
                title = "Résultat d'Exploitation",
                value = report.resExploitationVal,
                isRevenue = report.resExploitationVal >= 0,
                isExpanded = expandExploit,
                onToggleExpand = { expandExploit = !expandExploit },
                detailsList = report.resExploitationDetails,
                subText = "Performance opérationnelle globale",
                testTag = "cr_card_explo"
            )
        }

        // 5. Résultat Financier
        item {
            FinancialReportingCard(
                title = "Résultat Financier",
                value = report.resFinancierVal,
                isRevenue = report.resFinancierVal >= 0,
                isExpanded = expandFin,
                onToggleExpand = { expandFin = !expandFin },
                detailsList = report.resFinancierDetails,
                subText = "Classe 76 (Revenus) - Classe 66 (Charges)",
                testTag = "cr_card_fin"
            )
        }

        // 6. Résultat Exceptionnel
        item {
            FinancialReportingCard(
                title = "Résultat Exceptionnel",
                value = report.resExceptionnelVal,
                isRevenue = report.resExceptionnelVal >= 0,
                isExpanded = expandExc,
                onToggleExpand = { expandExc = !expandExc },
                detailsList = report.resExceptionnelDetails,
                subText = "Opérations non récurrentes",
                testTag = "cr_card_exc"
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FinancialReportingCard(
    title: String,
    value: Double,
    isRevenue: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    detailsList: List<AccountDetail>,
    subText: String? = null,
    testTag: String = ""
) {
    val darkBlue = Color(0xFF1D1B20)
    val lightGrey = Color(0xFFF7F2FA)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main clickable Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    )
                    if (subText != null) {
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = darkBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${String.format("%,.2f", value)} Ar",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isRevenue) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    )

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand details",
                        tint = darkBlue.copy(alpha = 0.6f)
                    )
                }
            }

            // Expandable details drawer
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGrey)
                        .padding(16.dp)
                ) {
                    if (detailsList.isEmpty()) {
                        Text(
                            text = "Aucune écriture enregistrée pour cet indicateur ce mois-ci.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        // Displaying ledger rows
                        Text(
                            text = "Détail des écritures d'origine :",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = darkBlue),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        detailsList.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (item.libelle.isNotBlank()) item.libelle else "Écriture Journal",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = darkBlue)
                                    )
                                    Row {
                                        Text(
                                            text = "Compte ${item.account}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = darkBlue.copy(alpha = 0.6f)
                                            )
                                        )
                                        if (item.date.isNotBlank()) {
                                            Text(
                                                text = " | Le ${item.date}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = darkBlue.copy(alpha = 0.5f))
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (item.type == "Débit") Color(0xFFEFF6FF) else Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = if (item.type == "Débit") "Déb" else "Cré",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.type == "Débit") Color(0xFF2563EB) else Color(0xFF047857)
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${String.format("%,.2f", item.baseAmount)} Ar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = darkBlue
                                        )
                                    )
                                }
                            }
                            HorizontalDivider(color = darkBlue.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
