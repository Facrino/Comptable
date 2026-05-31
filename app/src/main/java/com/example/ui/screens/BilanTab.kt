package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun BilanTab(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.balanceSheet.collectAsStateWithLifecycle()

    val primaryBg = Color(0xFFFDF7FF)
    val slateBlue = Color(0xFF1D1B20)
    val assetBlue = Color(0xFF6750A4)
    val liabilityGreen = Color(0xFF0F9D58)

    // Expandable states for the 8 requested categories
    var expActifCourant by remember { mutableStateOf(false) }
    var expActifNonCourant by remember { mutableStateOf(false) }
    var expDisponibilites by remember { mutableStateOf(false) }
    var expPassifCourant by remember { mutableStateOf(false) }
    var expPassifNonCourant by remember { mutableStateOf(false) }
    var expDetteLong by remember { mutableStateOf(false) }
    var expDetteCourt by remember { mutableStateOf(false) }
    var expDecouvert by remember { mutableStateOf(false) }

    // Calculations
    val totalActif = report.actifCourantVal + report.actifNonCourantVal + report.disponibilitesVal
    val totalPassif = report.passifCourantVal + report.passifNonCourantVal + report.detteLongTermeVal + report.detteCourtTermeVal + report.decouvertBancaireVal
    val diff = totalActif - totalPassif
    val isBalanced = Math.abs(diff) < 0.01

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(primaryBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Balance Equilibrium Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isBalanced) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isBalanced) Color(0xFFBBF7D0) else Color(0xFFFCA5A5),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Équilibre du Bilan du Mois",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = slateBlue)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOTAL ACTIFS (Emplois)", style = MaterialTheme.typography.labelSmall, color = slateBlue.copy(alpha = 0.6f))
                            Text(
                                "${String.format("%,.2f", totalActif)} Ar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = assetBlue)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL PASSIFS & CP (Ressources)", style = MaterialTheme.typography.labelSmall, color = slateBlue.copy(alpha = 0.6f))
                            Text(
                                "${String.format("%,.2f", totalPassif)} Ar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = liabilityGreen)
                            )
                        }
                    }

                    HorizontalDivider(color = slateBlue.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isBalanced) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Équilibré", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bilan parfaitement équilibré (Écart : 0.00 Ar)", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF166534), fontWeight = FontWeight.SemiBold))
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Déséquilibré", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Déséquilibre détecté ! Écart de ${String.format("%.2f", Math.abs(diff))} Ar", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF991B1B), fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }
        }

        // Section: Assets Header
        item {
            Text(
                text = "ACTIFS (Ce que l'entreprise possède)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = assetBlue),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // 1. Actif Courant
        item {
            BilanCategoryCard(
                title = "Actifs courants (Stocks & créances)",
                value = report.actifCourantVal,
                isAsset = true,
                isExpanded = expActifCourant,
                onToggleExpand = { expActifCourant = !expActifCourant },
                detailsList = report.actifCourantDetails,
                testTag = "bilan_card_ac"
            )
        }

        // 2. Actifs non courant
        item {
            BilanCategoryCard(
                title = "Actifs non courants (Immobilisations)",
                value = report.actifNonCourantVal,
                isAsset = true,
                isExpanded = expActifNonCourant,
                onToggleExpand = { expActifNonCourant = !expActifNonCourant },
                detailsList = report.actifNonCourantDetails,
                testTag = "bilan_card_anc"
            )
        }

        // 3. Disponibilités
        item {
            BilanCategoryCard(
                title = "Disponibilités (Trésorerie active)",
                value = report.disponibilitesVal,
                isAsset = true,
                isExpanded = expDisponibilites,
                onToggleExpand = { expDisponibilites = !expDisponibilites },
                detailsList = report.disponibilitesDetails,
                testTag = "bilan_card_disp"
            )
        }

        // Section: Liabilities Header
        item {
            Text(
                text = "PASSIFS & CAPITAUX PROPRES (Ce que l'entreprise doit)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = liabilityGreen),
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        // 4. Passif Courant
        item {
            BilanCategoryCard(
                title = "Passifs courants (Dettes CT)",
                value = report.passifCourantVal,
                isAsset = false,
                isExpanded = expPassifCourant,
                onToggleExpand = { expPassifCourant = !expPassifCourant },
                detailsList = report.passifCourantDetails,
                testTag = "bilan_card_pc"
            )
        }

        // 5. Passif non courant
        item {
            BilanCategoryCard(
                title = "Passifs non courants (Capitaux propres)",
                value = report.passifNonCourantVal,
                isAsset = false,
                isExpanded = expPassifNonCourant,
                onToggleExpand = { expPassifNonCourant = !expPassifNonCourant },
                detailsList = report.passifNonCourantDetails,
                testTag = "bilan_card_pnc"
            )
        }

        // 6. Dette à long terme
        item {
            BilanCategoryCard(
                title = "Dettes à long terme (Emprunts > 1 an)",
                value = report.detteLongTermeVal,
                isAsset = false,
                isExpanded = expDetteLong,
                onToggleExpand = { expDetteLong = !expDetteLong },
                detailsList = report.detteLongTermeDetails,
                testTag = "bilan_card_dlt"
            )
        }

        // 7. Dette à courte
        item {
            BilanCategoryCard(
                title = "Dettes à court terme (Trésorerie passive)",
                value = report.detteCourtTermeVal,
                isAsset = false,
                isExpanded = expDetteCourt,
                onToggleExpand = { expDetteCourt = !expDetteCourt },
                detailsList = report.detteCourtTermeDetails,
                testTag = "bilan_card_dct"
            )
        }

        // 8. Découvert bancaire
        item {
            BilanCategoryCard(
                title = "Découverts bancaires",
                value = report.decouvertBancaireVal,
                isAsset = false,
                isExpanded = expDecouvert,
                onToggleExpand = { expDecouvert = !expDecouvert },
                detailsList = report.decouvertBancaireDetails,
                testTag = "bilan_card_dec"
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BilanCategoryCard(
    title: String,
    value: Double,
    isAsset: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    detailsList: List<AccountDetail>,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${String.format("%,.2f", value)} Ar",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isAsset) Color(0xFF6750A4) else Color(0xFF0F9D58)
                        )
                    )

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = darkBlue.copy(alpha = 0.6f)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGrey)
                        .padding(16.dp)
                ) {
                    if (detailsList.isEmpty()) {
                        Text(
                            text = "Aucune écriture d'origine pour cette rubrique.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    } else {
                        Text(
                            text = "Calculés d'après les soldes de comptes :",
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
                                        text = "Compte ${item.account}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = darkBlue
                                        )
                                    )
                                    Text(
                                        text = if (item.libelle.isNotBlank()) item.libelle else "Solde de compte d'origine",
                                        style = MaterialTheme.typography.labelSmall.copy(color = darkBlue.copy(alpha = 0.5f))
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (item.type == "Débit") Color(0xFFEFF6FF) else Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = if (item.type == "Débit") "D" else "C",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.type == "Débit") Color(0xFF2563EB) else Color(0xFF047857)
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    val isNegative = (isAsset && item.type == "Crédit") || (!isAsset && item.type == "Débit")
                                    val formattedAmount = if (isNegative) {
                                        "-${String.format("%,.2f", item.baseAmount)}"
                                    } else {
                                        String.format("%,.2f", item.baseAmount)
                                    }
                                    Text(
                                        text = "$formattedAmount Ar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNegative) Color(0xFFEF4444) else darkBlue
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
