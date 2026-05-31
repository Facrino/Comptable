package com.example.data

import kotlin.math.abs

data class AccountDetail(
    val account: String,
    val date: String,
    val libelle: String,
    val type: String, // "Débit" or "Crédit"
    val baseAmount: Double,
    val transactionId: Int
)

data class IncomeStatementReport(
    val yearMonth: String,
    val caVal: Double,
    val caDetails: List<AccountDetail>,
    val consoVal: Double,
    val consoDetails: List<AccountDetail>,
    val ebeVal: Double,
    val ebeDetails: List<AccountDetail>,
    val resExploitationVal: Double,
    val resExploitationDetails: List<AccountDetail>,
    val resFinancierVal: Double,
    val resFinancierDetails: List<AccountDetail>,
    val resExceptionnelVal: Double,
    val resExceptionnelDetails: List<AccountDetail>
)

data class BalanceSheetReport(
    val yearMonth: String,
    val actifCourantVal: Double,
    val actifCourantDetails: List<AccountDetail>,
    val actifNonCourantVal: Double,
    val actifNonCourantDetails: List<AccountDetail>,
    val disponibilitesVal: Double,
    val disponibilitesDetails: List<AccountDetail>,
    val passifCourantVal: Double,
    val passifCourantDetails: List<AccountDetail>,
    val passifNonCourantVal: Double,
    val passifNonCourantDetails: List<AccountDetail>,
    val detteLongTermeVal: Double,
    val detteLongTermeDetails: List<AccountDetail>,
    val detteCourtTermeVal: Double,
    val detteCourtTermeDetails: List<AccountDetail>,
    val decouvertBancaireVal: Double,
    val decouvertBancaireDetails: List<AccountDetail>
)

object AccountingEngine {

    // Help check if a date falls into the filter month (format "YYYY-MM")
    private fun isSameMonth(dateStr: String, filterMonth: String): Boolean {
        // dateStr is typically "YYYY-MM-DD" or similar
        return dateStr.startsWith(filterMonth)
    }

    // Gathers all flat ledger lines from transactions
    // Each transaction produces a primary debit line, a primary credit line, and any accompaniments
    fun extractLedgerLines(transactions: List<Transaction>, filterMonth: String): List<AccountDetail> {
        val lines = mutableListOf<AccountDetail>()
        val filteredTx = transactions.filter { isSameMonth(it.date, filterMonth) }

        for (tx in filteredTx) {
            // Primary Debit
            lines.add(
                AccountDetail(
                    account = tx.debitAccount,
                    date = tx.date,
                    libelle = tx.libelle,
                    type = "Débit",
                    baseAmount = tx.debitAmount,
                    transactionId = tx.id
                )
            )

            // Primary Credit
            lines.add(
                AccountDetail(
                    account = tx.creditAccount,
                    date = tx.date,
                    libelle = tx.libelle,
                    type = "Crédit",
                    baseAmount = tx.creditAmount,
                    transactionId = tx.id
                )
            )

            // Accompaniments
            for (acc in tx.getAccompaniments()) {
                lines.add(
                    AccountDetail(
                        account = acc.account,
                        date = tx.date,
                        libelle = "${tx.libelle} (${acc.label})",
                        type = if (acc.isDebit) "Débit" else "Crédit",
                        baseAmount = acc.amount,
                        transactionId = tx.id
                    )
                )
            }
        }
        return lines
    }

    fun generateIncomeStatement(transactions: List<Transaction>, filterMonth: String): IncomeStatementReport {
        val ledger = extractLedgerLines(transactions, filterMonth)

        // 1. Chiffre d'Affaires (CA) -> Class 70, 71, 72 ... mostly starts with "7" (excluding finanacial 76 or exceptional 77)
        // CA = Credit start with "7" minus Debit start with "7" (excluding 76 & 77)
        val caDetails = ledger.filter { it.account.startsWith("7") && !it.account.startsWith("76") && !it.account.startsWith("77") }
        val caVal = caDetails.sumOf { if (it.type == "Crédit") it.baseAmount else -it.baseAmount }

        // 2. Consommation -> Class 60 (Achats, variation stock), 61 & 62 (Services extérieurs)
        val consoDetails = ledger.filter { it.account.startsWith("60") || it.account.startsWith("61") || it.account.startsWith("62") }
        val consoVal = consoDetails.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }

        // 3. EBE (Excédent Brut d'Exploitation)
        // EBE = CA - Consommation - Charges de personnel (64) - Impôts et taxes (63)
        val ebeRevenueDetails = ledger.filter { it.account.startsWith("7") && !it.account.startsWith("76") && !it.account.startsWith("77") }
        val ebeExpenseDetails = ledger.filter { 
            it.account.startsWith("60") || it.account.startsWith("61") || it.account.startsWith("62") ||
            it.account.startsWith("63") || it.account.startsWith("64") 
        }
        
        // EBE details show all these operating elements
        val ebeDetails = ebeRevenueDetails + ebeExpenseDetails
        val chargesPersonnel = ledger.filter { it.account.startsWith("64") }.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }
        val impotsTaxes = ledger.filter { it.account.startsWith("63") }.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }
        val ebeVal = caVal - consoVal - chargesPersonnel - impotsTaxes

        // 4. Résultat d'Exploitation
        // Operating Result = EBE + other operating revenues (75) - other operating charges (65) - Dotations aux Amorts (68)
        val otherOpRevenues = ledger.filter { it.account.startsWith("75") }.sumOf { if (it.type == "Crédit") it.baseAmount else -it.baseAmount }
        val otherOpExpenses = ledger.filter { it.account.startsWith("65") }.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }
        val dotationsAmort = ledger.filter { it.account.startsWith("68") }.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }
        
        val resExploitationVal = ebeVal + otherOpRevenues - otherOpExpenses - dotationsAmort
        val resExploitationDetails = ledger.filter { 
            (it.account.startsWith("6") || it.account.startsWith("7")) && 
            !it.account.startsWith("66") && !it.account.startsWith("67") && 
            !it.account.startsWith("76") && !it.account.startsWith("77") 
        }

        // 5. Résultat Financier -> Class 76 (Products) minus Class 66 (Charges)
        val finRevenues = ledger.filter { it.account.startsWith("76") }
        val finExpenses = ledger.filter { it.account.startsWith("66") }
        val resFinancierDetails = finRevenues + finExpenses
        val resFinancierVal = finRevenues.sumOf { if (it.type == "Crédit") it.baseAmount else -it.baseAmount } - 
                              finExpenses.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }

        // 6. Résultat Exceptionnel -> Class 77 (Products) minus Class 67 (Charges)
        val excRevenues = ledger.filter { it.account.startsWith("77") }
        val excExpenses = ledger.filter { it.account.startsWith("67") }
        val resExceptionnelDetails = excRevenues + excExpenses
        val resExceptionnelVal = excRevenues.sumOf { if (it.type == "Crédit") it.baseAmount else -it.baseAmount } - 
                                 excExpenses.sumOf { if (it.type == "Débit") it.baseAmount else -it.baseAmount }

        return IncomeStatementReport(
            yearMonth = filterMonth,
            caVal = caVal,
            caDetails = caDetails.sortedBy { it.account },
            consoVal = consoVal,
            consoDetails = consoDetails.sortedBy { it.account },
            ebeVal = ebeVal,
            ebeDetails = ebeDetails.sortedBy { it.account },
            resExploitationVal = resExploitationVal,
            resExploitationDetails = resExploitationDetails.sortedBy { it.account },
            resFinancierVal = resFinancierVal,
            resFinancierDetails = resFinancierDetails.sortedBy { it.account },
            resExceptionnelVal = resExceptionnelVal,
            resExceptionnelDetails = resExceptionnelDetails.sortedBy { it.account }
        )
    }

    fun generateBalanceSheet(transactions: List<Transaction>, filterMonth: String): BalanceSheetReport {
        val ledger = extractLedgerLines(transactions, filterMonth)

        // Aggregates net balance per account
        // Accounts starting with 1, 2, 3, 4, 5 belong here
        val accountsMap = ledger.filter { 
            it.account.startsWith("1") || it.account.startsWith("2") || 
            it.account.startsWith("3") || it.account.startsWith("4") || 
            it.account.startsWith("5") 
        }.groupBy { it.account }

        // Actif Non Courant (Class 2: Immobilisations)
        val ancDetails = mutableListOf<AccountDetail>()
        var ancVal = 0.0
        accountsMap.filter { it.key.startsWith("2") }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = d - c
            if (net != 0.0) {
                ancVal += net
                ancDetails.add(AccountDetail(account, "", "Solde net Immobilisation", if (net > 0) "Débit" else "Crédit", abs(net), 0))
            }
        }

        // Actif Courant (Class 3: Stocks, Class 41: Clients, Class 445: State/TVA deductible etc.)
        val acDetails = mutableListOf<AccountDetail>()
        var acVal = 0.0
        accountsMap.filter { 
            it.key.startsWith("3") || 
            it.key.startsWith("41") || 
            it.key.startsWith("4456") // TVA Déductible 
        }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = d - c
            if (net != 0.0) {
                acVal += net
                acDetails.add(AccountDetail(account, "", "Solde net actif", if (net > 0) "Débit" else "Crédit", abs(net), 0))
            }
        }

        // Passif Non Courant (Class 10: Capital, Class 12: Résultat, Class 15: Provisions) - excluding 16 long-term debts and excluding dynamic 12 accounts (calculated below)
        val pncDetails = mutableListOf<AccountDetail>()
        var pncVal = 0.0
        accountsMap.filter { it.key.startsWith("1") && !it.key.startsWith("16") && !it.key.startsWith("12") }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = c - d // Passif increases under credit
            if (net != 0.0) {
                pncVal += net
                pncDetails.add(AccountDetail(account, "", "Solde net capitaux & provisions", if (net > 0) "Crédit" else "Débit", abs(net), 0))
            }
        }

        // Dynamically compute and inject the Monthly Net Income to balance the balance sheet perfectly
        val incomeReport = generateIncomeStatement(transactions, filterMonth)
        val netResult = incomeReport.resExploitationVal + incomeReport.resFinancierVal + incomeReport.resExceptionnelVal
        if (netResult != 0.0) {
            pncVal += netResult
            pncDetails.add(
                AccountDetail(
                    account = if (netResult >= 0) "120" else "129",
                    date = "",
                    libelle = if (netResult >= 0) "Bénéfice de l'exercice (Résultat Net)" else "Perte de l'exercice (Résultat Net)",
                    type = if (netResult >= 0) "Crédit" else "Débit",
                    baseAmount = abs(netResult),
                    transactionId = 0
                )
            )
        }

        // Passif Courant (Class 40, 42, 43, 444, 4455, 4457, 455)
        val pcDetails = mutableListOf<AccountDetail>()
        var pcVal = 0.0
        accountsMap.filter { 
            it.key.startsWith("40") || 
            it.key.startsWith("42") || 
            it.key.startsWith("43") || 
            it.key.startsWith("444") ||
            it.key.startsWith("4455") ||
            it.key.startsWith("4457") || // TVA Collectée
            it.key.startsWith("455")
        }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = c - d // liabilities increase under credit
            if (net != 0.0) {
                pcVal += net
                pcDetails.add(AccountDetail(account, "", "Solde net passif courant", if (net > 0) "Crédit" else "Débit", abs(net), 0))
            }
        }

        // Long-term Debt (Dette à long terme: Class 16)
        val dltDetails = mutableListOf<AccountDetail>()
        var dltVal = 0.0
        accountsMap.filter { it.key.startsWith("16") }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = c - d
            if (net != 0.0) {
                dltVal += net
                dltDetails.add(AccountDetail(account, "", "Solde net Emprunts", if (net > 0) "Crédit" else "Débit", abs(net), 0))
            }
        }

        // Short-term Debt (Dette à court terme: Class 519 or other short-term loans, excluding supplier running liabilities which are passif courant)
        val dctDetails = mutableListOf<AccountDetail>()
        var dctVal = 0.0
        accountsMap.filter { it.key.startsWith("519") }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = c - d
            if (net != 0.0) {
                dctVal += net
                dctDetails.add(AccountDetail(account, "", "Solde net concours bancaires CT", if (net > 0) "Crédit" else "Débit", abs(net), 0))
            }
        }

        // Cash/Availability & Bank Overdraft calculation
        // All accounts starting with 5 (excluding 519)
        // Group these to find if Banque is in debit or credit
        val disponibilitesDetails = mutableListOf<AccountDetail>()
        var disponibilitesVal = 0.0
        val decouvertBancaireDetails = mutableListOf<AccountDetail>()
        var decouvertBancaireVal = 0.0

        accountsMap.filter { it.key.startsWith("5") && !it.key.startsWith("519") }.forEach { (account, lines) ->
            val d = lines.filter { it.type == "Débit" }.sumOf { it.baseAmount }
            val c = lines.filter { it.type == "Crédit" }.sumOf { it.baseAmount }
            val net = d - c

            if (net > 0) {
                // Bank is in positive (Asset)
                disponibilitesVal += net
                disponibilitesDetails.add(AccountDetail(account, "", "Disponibilités (solde débiteur)", "Débit", net, 0))
            } else if (net < 0) {
                // Bank is in overdraft (Liability)
                decouvertBancaireVal += abs(net)
                decouvertBancaireDetails.add(AccountDetail(account, "", "Découvert bancaire (solde créditeur)", "Crédit", abs(net), 0))
            }
        }

        return BalanceSheetReport(
            yearMonth = filterMonth,
            actifCourantVal = acVal,
            actifCourantDetails = acDetails.sortedBy { it.account },
            actifNonCourantVal = ancVal,
            actifNonCourantDetails = ancDetails.sortedBy { it.account },
            disponibilitesVal = disponibilitesVal,
            disponibilitesDetails = disponibilitesDetails.sortedBy { it.account },
            passifCourantVal = pcVal,
            passifCourantDetails = pcDetails.sortedBy { it.account },
            passifNonCourantVal = pncVal,
            passifNonCourantDetails = pncDetails.sortedBy { it.account },
            detteLongTermeVal = dltVal,
            detteLongTermeDetails = dltDetails.sortedBy { it.account },
            detteCourtTermeVal = dctVal,
            detteCourtTermeDetails = dctDetails.sortedBy { it.account },
            decouvertBancaireVal = decouvertBancaireVal,
            decouvertBancaireDetails = decouvertBancaireDetails.sortedBy { it.account }
        )
    }
}
