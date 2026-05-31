package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = TransactionRepository(db.transactionDao())

    private val sharedPrefs = application.getSharedPreferences("zaina_compta_prefs", android.content.Context.MODE_PRIVATE)

    // UI state for authentication
    private val _isLoggedIn = MutableStateFlow(sharedPrefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(sharedPrefs.getString("user_email", "") ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow(sharedPrefs.getString("user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userFaculte = MutableStateFlow(sharedPrefs.getString("user_faculte", "") ?: "")
    val userFaculte: StateFlow<String> = _userFaculte.asStateFlow()

    private val _userParcours = MutableStateFlow(sharedPrefs.getString("user_parcours", "") ?: "")
    val userParcours: StateFlow<String> = _userParcours.asStateFlow()

    private val _userNiveau = MutableStateFlow(sharedPrefs.getString("user_niveau", "") ?: "")
    val userNiveau: StateFlow<String> = _userNiveau.asStateFlow()

    // UI state for selected reporting month (format: "YYYY-MM")
    private val _selectedMonth = MutableStateFlow("2026-05") // matches current time in metadata (May 2026)
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // Transactions list
    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Income Statement Report
    val incomeStatement: StateFlow<IncomeStatementReport> = combine(allTransactions, _selectedMonth) { list, month ->
        AccountingEngine.generateIncomeStatement(list, month)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeStatementReport("2026-05", 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList())
    )

    // Current Balance Sheet Report
    val balanceSheet: StateFlow<BalanceSheetReport> = combine(allTransactions, _selectedMonth) { list, month ->
        AccountingEngine.generateBalanceSheet(list, month)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BalanceSheetReport("2026-05", 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList(), 0.0, emptyList())
    )

    // Cloud Backup / Sync Status State
    private val _syncStatus = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncStatus: StateFlow<SyncState> = _syncStatus.asStateFlow()

    sealed interface SyncState {
        object Idle : SyncState
        object Syncing : SyncState
        data class Success(val message: String, val lastBackupJson: String = "") : SyncState
        data class Error(val error: String) : SyncState
    }

    init {
        // Pre-populate database with beautiful, balanced, representative sample accounting operations
        // whenever the database starts empty!
        viewModelScope.launch {
            repository.allTransactions.first { true }.let { currentList ->
                if (currentList.isEmpty()) {
                    populateSampleEntries()
                }
            }
        }
    }

    fun login(email: String, name: String) {
        viewModelScope.launch {
            sharedPrefs.edit().apply {
                putBoolean("is_logged_in", true)
                putString("user_email", email)
                putString("user_name", name)
                apply()
            }
            _userEmail.value = email
            _userName.value = name
            _isLoggedIn.value = true
        }
    }

    fun updateProfile(name: String, faculte: String, parcours: String, niveau: String) {
        sharedPrefs.edit().apply {
            putString("user_name", name)
            putString("user_faculte", faculte)
            putString("user_parcours", parcours)
            putString("user_niveau", niveau)
            apply()
        }
        _userName.value = name
        _userFaculte.value = faculte
        _userParcours.value = parcours
        _userNiveau.value = niveau
    }

    fun logout() {
        sharedPrefs.edit().apply {
            putBoolean("is_logged_in", false)
            putString("user_email", "")
            putString("user_name", "")
            putString("user_faculte", "")
            putString("user_parcours", "")
            putString("user_niveau", "")
            apply()
        }
        _isLoggedIn.value = false
        _userEmail.value = ""
        _userName.value = ""
        _userFaculte.value = ""
        _userParcours.value = ""
        _userNiveau.value = ""
    }

    fun changeMonth(yearMonth: String) {
        _selectedMonth.value = yearMonth
    }

    fun addTransaction(
        date: String,
        libelle: String,
        debitAccount: String,
        debitAmount: Double,
        creditAccount: String,
        creditAmount: Double,
        accompaniments: List<Accompaniment>
    ) {
        viewModelScope.launch {
            val accompanimentsJson = Transaction.convertAccompanimentsToJson(accompaniments)
            val tx = Transaction(
                date = date,
                libelle = libelle,
                debitAccount = debitAccount,
                debitAmount = debitAmount,
                creditAccount = creditAccount,
                creditAmount = creditAmount,
                accompanimentsJson = accompanimentsJson
            )
            repository.insert(tx)
        }
    }

    fun editTransaction(
        id: Int,
        date: String,
        libelle: String,
        debitAccount: String,
        debitAmount: Double,
        creditAccount: String,
        creditAmount: Double,
        accompaniments: List<Accompaniment>
    ) {
        viewModelScope.launch {
            val accompanimentsJson = Transaction.convertAccompanimentsToJson(accompaniments)
            val tx = Transaction(
                id = id,
                date = date,
                libelle = libelle,
                debitAccount = debitAccount,
                debitAmount = debitAmount,
                creditAccount = creditAccount,
                creditAmount = creditAmount,
                accompanimentsJson = accompanimentsJson
            )
            repository.update(tx)
        }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            repository.delete(tx)
        }
    }

    // Backup current ledger entries to cloud simulated JSON file
    fun backupToCloud() {
        viewModelScope.launch {
            _syncStatus.value = SyncState.Syncing
            try {
                val txs = allTransactions.value
                val mainArray = JSONArray()
                for (t in txs) {
                    val obj = JSONObject().apply {
                        put("id", t.id)
                        put("date", t.date)
                        put("libelle", t.libelle)
                        put("debitAccount", t.debitAccount)
                        put("debitAmount", t.debitAmount)
                        put("creditAccount", t.creditAccount)
                        put("creditAmount", t.creditAmount)
                        put("accompanimentsJson", t.accompanimentsJson)
                    }
                    mainArray.put(obj)
                }
                val resultJson = mainArray.toString(2)
                
                // Simulate a fast cloud network delay
                kotlinx.coroutines.delay(1200)
                _syncStatus.value = SyncState.Success(
                    message = "Sauvegarde réussie dans le Cloud sécurisé (${txs.size} écritures synchronisées) !",
                    lastBackupJson = resultJson
                )
            } catch (e: Exception) {
                _syncStatus.value = SyncState.Error("Échec de la sauvegarde : ${e.localizedMessage}")
            }
        }
    }

    // Restore transactions list from backup JSON string
    fun restoreFromCloud(backupJson: String) {
        viewModelScope.launch {
            _syncStatus.value = SyncState.Syncing
            try {
                if (backupJson.isBlank()) {
                    _syncStatus.value = SyncState.Error("Erreur : Aucun fichier de sauvegarde valide trouvé ou spécifié.")
                    return@launch
                }
                
                val array = JSONArray(backupJson)
                val newList = mutableListOf<Transaction>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    newList.add(
                        Transaction(
                            id = obj.optInt("id", 0),
                            date = obj.getString("date"),
                            libelle = obj.getString("libelle"),
                            debitAccount = obj.getString("debitAccount"),
                            debitAmount = obj.getDouble("debitAmount"),
                            creditAccount = obj.getString("creditAccount"),
                            creditAmount = obj.getDouble("creditAmount"),
                            accompanimentsJson = obj.optString("accompanimentsJson", "[]")
                        )
                    )
                }

                // Delete current, then restore
                repository.deleteAll()
                repository.insertAll(newList)

                kotlinx.coroutines.delay(1000)
                _syncStatus.value = SyncState.Success("Restauration réussie ! ${newList.size} écritures récupérées.")
            } catch (e: Exception) {
                _syncStatus.value = SyncState.Error("Échec de la restauration : ${e.localizedMessage}")
            }
        }
    }

    fun clearSyncStatus() {
        _syncStatus.value = SyncState.Idle
    }

    private suspend fun populateSampleEntries() {
        val sampleList = listOf(
            // May 2026 transactions
            Transaction(
                date = "2026-05-02",
                libelle = "Facture Client Durand - Vente matériels",
                debitAccount = "411000", // Propriété Actif Courant (Clients)
                debitAmount = 6000.0,
                creditAccount = "707000", // CA (Ventes de marchandises)
                creditAmount = 5000.0,
                accompanimentsJson = Transaction.convertAccompanimentsToJson(
                    listOf(
                        Accompaniment("TVA Collectée 20%", false, "445710", 1000.0) // Passif Courant
                    )
                )
            ),
            Transaction(
                date = "2026-05-10",
                libelle = "Achat Fournisseur Bureau Vallée",
                debitAccount = "606400", // Consommation (Fournitures de bureau)
                debitAmount = 1000.0,
                creditAccount = "401000", // Fournisseurs (Passif Courant)
                creditAmount = 1200.0,
                accompanimentsJson = Transaction.convertAccompanimentsToJson(
                    listOf(
                        Accompaniment("TVA Déductible 20%", true, "445660", 200.0) // Actif Courant
                    )
                )
            ),
            Transaction(
                date = "2026-05-15",
                libelle = "Frais de déplacement - Mission client Lyon",
                debitAccount = "625100", // Consommation (Voyages et déplacements)
                debitAmount = 300.0,
                creditAccount = "512000", // Banque (Passif if Credit, default is Balance Sheet bank)
                creditAmount = 360.0,
                accompanimentsJson = Transaction.convertAccompanimentsToJson(
                    listOf(
                        Accompaniment("Frais de transport annexes", true, "624100", 60.0) // Debit extra consumable
                    )
                )
            ),
            Transaction(
                date = "2026-05-20",
                libelle = "Financement d'Achat d'Ordinateur Mac",
                debitAccount = "218300", // Immobilisation matériels informatique (Actif non courant)
                debitAmount = 2500.0,
                creditAccount = "512000", // Banque
                creditAmount = 3000.0,
                accompanimentsJson = Transaction.convertAccompanimentsToJson(
                    listOf(
                        Accompaniment("TVA Déductible Matériel 20%", true, "445620", 500.0) // Actif courant
                    )
                )
            ),
            Transaction(
                date = "2026-05-25",
                libelle = "Paiement de salaires du mois",
                debitAccount = "641000", // Charges de personnel
                debitAmount = 3500.0,
                creditAccount = "512000", // Banque
                creditAmount = 3500.0
            ),
            Transaction(
                date = "2026-05-28",
                libelle = "Intérêts d'Emprunt bancaire LCL",
                debitAccount = "661100", // Charges d'intérêts (Résultat Financier)
                debitAmount = 140.0,
                creditAccount = "512000", // Banque
                creditAmount = 140.0
            ),
            Transaction(
                date = "2026-05-31",
                libelle = "Apport personnel Capital d'origine",
                debitAccount = "512000", // Banque (positive Debit balance starts)
                debitAmount = 25000.0,
                creditAccount = "101000", // Capital (Passif Non Courant - Capitaux propres)
                creditAmount = 25000.0
            )
        )

        for (tx in sampleList) {
            repository.insert(tx)
        }
    }
}
