package com.example

import com.example.data.AccountingEngine
import com.example.data.Transaction
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBalanceSheetAndIncomeWithProfit() {
        // Start with:
        // Capital (101): Credit 50000
        // Bank (512): Debit 50000
        val t1 = Transaction(
            id = 1,
            date = "2026-05-15",
            libelle = "Capital initial",
            debitAccount = "512",
            debitAmount = 50000.0,
            creditAccount = "101",
            creditAmount = 50000.0
        )

        // Then do some sales:
        // Client (411): Debit 10000
        // Prestations (706): Credit 10000
        val t2 = Transaction(
            id = 2,
            date = "2026-05-20",
            libelle = "Vente prestation",
            debitAccount = "411",
            debitAmount = 10000.0,
            creditAccount = "706",
            creditAmount = 10000.0
        )

        val list = listOf(t1, t2)
        val month = "2026-05"

        val income = AccountingEngine.generateIncomeStatement(list, month)
        assertEquals(10000.0, income.caVal, 0.01)

        val netResult = income.resExploitationVal + income.resFinancierVal + income.resExceptionnelVal
        assertEquals(10000.0, netResult, 0.01)

        val balanceSheet = AccountingEngine.generateBalanceSheet(list, month)
        
        // Calculations in UI
        val totalActif = balanceSheet.actifCourantVal + balanceSheet.actifNonCourantVal + balanceSheet.disponibilitesVal
        val totalPassif = balanceSheet.passifCourantVal + balanceSheet.passifNonCourantVal + balanceSheet.detteLongTermeVal + balanceSheet.detteCourtTermeVal + balanceSheet.decouvertBancaireVal

        println("Profit Scenario Actif total: $totalActif")
        println("Profit Scenario Passif total: $totalPassif")
        println("PNC details: ${balanceSheet.passifNonCourantDetails}")

        assertEquals(totalActif, totalPassif, 0.01)
    }

    @Test
    fun testBalanceSheetAndIncomeWithLoss() {
        // Start with:
        // Capital (101): Credit 50000
        // Bank (512): Debit 50000
        val t1 = Transaction(
            id = 1,
            date = "2026-05-15",
            libelle = "Capital initial",
            debitAccount = "512",
            debitAmount = 50000.0,
            creditAccount = "101",
            creditAmount = 50000.0
        )

        // Then do some purchase/expense:
        // Location (613): Debit 12000
        // Bank (512): Credit 12000
        val t2 = Transaction(
            id = 2,
            date = "2026-05-20",
            libelle = "Paiement Loyer",
            debitAccount = "613",
            debitAmount = 12000.0,
            creditAccount = "512",
            creditAmount = 12000.0
        )

        val list = listOf(t1, t2)
        val month = "2026-05"

        val income = AccountingEngine.generateIncomeStatement(list, month)
        val netResult = income.resExploitationVal + income.resFinancierVal + income.resExceptionnelVal
        assertEquals(-12000.0, netResult, 0.01)

        val balanceSheet = AccountingEngine.generateBalanceSheet(list, month)
        
        // Calculations in UI
        val totalActif = balanceSheet.actifCourantVal + balanceSheet.actifNonCourantVal + balanceSheet.disponibilitesVal
        val totalPassif = balanceSheet.passifCourantVal + balanceSheet.passifNonCourantVal + balanceSheet.detteLongTermeVal + balanceSheet.detteCourtTermeVal + balanceSheet.decouvertBancaireVal

        println("Loss Scenario Actif total: $totalActif")
        println("Loss Scenario Passif total: $totalPassif")
        println("PNC details: ${balanceSheet.passifNonCourantDetails}")
        println("PNC Val: ${balanceSheet.passifNonCourantVal}")

        assertEquals(totalActif, totalPassif, 0.01)
    }
}
