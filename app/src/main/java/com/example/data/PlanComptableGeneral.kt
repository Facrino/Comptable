package com.example.data

data class PcgAccount(
    val code: String,
    val name: String,
    val category: String
)

object PlanComptableGeneral {
    val accounts = listOf(
        // Classe 1
        PcgAccount("101", "Capital", "Capitaux"),
        PcgAccount("1061", "Réserve légale", "Capitaux"),
        PcgAccount("1068", "Autres réserves", "Capitaux"),
        PcgAccount("108", "Compte de l'exploitant", "Capitaux"),
        PcgAccount("110", "Report à nouveau (solde créditeur)", "Capitaux"),
        PcgAccount("119", "Report à nouveau (solde débiteur)", "Capitaux"),
        PcgAccount("120", "Résultat de l'exercice - Bénéfice", "Capitaux"),
        PcgAccount("129", "Résultat de l'exercice - Perte", "Capitaux"),
        PcgAccount("164", "Emprunts auprès des établissements de crédit", "Dettes à long terme"),

        // Classe 2
        PcgAccount("201", "Frais d'établissement", "Immobilisations"),
        PcgAccount("205", "Concessions, brevets, licences, logiciels", "Immobilisations"),
        PcgAccount("211", "Terrains", "Immobilisations"),
        PcgAccount("213", "Constructions", "Immobilisations"),
        PcgAccount("215", "Installations techniques, matériel & outillage", "Immobilisations"),
        PcgAccount("2181", "Installations générales, agencements", "Immobilisations"),
        PcgAccount("2182", "Matériel de transport (Véhicules)", "Immobilisations"),
        PcgAccount("2183", "Matériel de bureau et informatique", "Immobilisations"),
        PcgAccount("2184", "Mobilier de bureau", "Immobilisations"),

        // Classe 3
        PcgAccount("311", "Matières premières", "Stocks"),
        PcgAccount("355", "Produits finis", "Stocks"),
        PcgAccount("371", "Marchandises", "Stocks"),

        // Classe 4
        PcgAccount("401", "Fournisseurs", "Tiers / Dettes courant"),
        PcgAccount("404", "Fournisseurs d'immobilisations", "Tiers / Dettes courant"),
        PcgAccount("411", "Clients", "Tiers / Actif courant"),
        PcgAccount("421", "Personnel - Rémunérations dues (Salaires net)", "Tiers / Dettes courant"),
        PcgAccount("431", "Sécurité sociale / URSSAF", "Tiers / Dettes courant"),
        PcgAccount("444", "État - Impôt sur les bénéfices", "Tiers / Dettes courant"),
        PcgAccount("4455", "TVA à décaisser", "Tiers / Dettes courant"),
        PcgAccount("44562", "TVA déductible sur immobilisations", "Tiers / Actif courant"),
        PcgAccount("44566", "TVA déductible sur autres biens et services", "Tiers / Actif courant"),
        PcgAccount("44571", "TVA collectée", "Tiers / Dettes courant"),
        PcgAccount("455", "Associés - Comptes courants", "Tiers / Dettes courant"),

        // Classe 5
        PcgAccount("503", "Actions / Valeurs mobilières de placement", "Trésorerie / Finances"),
        PcgAccount("512", "Banques (Compte courant principal)", "Trésorerie / Finances"),
        PcgAccount("514", "Chèques postaux", "Trésorerie / Finances"),
        PcgAccount("531", "Caisse (Espèces)", "Trésorerie / Finances"),

        // Classe 6
        PcgAccount("601", "Achats stockés - Matières premières", "Charges d'exploitation"),
        PcgAccount("602", "Achats stockés - Autres approvisionnements", "Charges d'exploitation"),
        PcgAccount("6061", "Fournitures non stockables (Eau, électricité, gaz, carburant)", "Charges d'exploitation"),
        PcgAccount("6063", "Fournitures d'entretien et de petit équipement", "Charges d'exploitation"),
        PcgAccount("6064", "Fournitures de bureau", "Charges d'exploitation"),
        PcgAccount("607", "Achats de marchandises", "Charges d'exploitation"),
        PcgAccount("611", "Sous-traitance générale", "Charges d'exploitation"),
        PcgAccount("613", "Locations de locaux / Matériels", "Charges d'exploitation"),
        PcgAccount("615", "Entretien et réparations", "Charges d'exploitation"),
        PcgAccount("616", "Primes d'assurances", "Charges d'exploitation"),
        PcgAccount("622", "Rémunérations d'intermédiaires et honoraires (Avocats, comptables)", "Charges d'exploitation"),
        PcgAccount("623", "Publicité, relations publiques", "Charges d'exploitation"),
        PcgAccount("624", "Transports de biens ou du personnel", "Charges d'exploitation"),
        PcgAccount("625", "Déplacements, missions et réceptions (Hôtels, repas)", "Charges d'exploitation"),
        PcgAccount("626", "Frais postaux et de télécommunications (Abonnement internet, téléphone)", "Charges d'exploitation"),
        PcgAccount("627", "Services bancaires (Frais de tenue de compte, commissions)", "Charges d'exploitation"),
        PcgAccount("631", "Impôts et taxes sur les rémunérations", "Charges d'exploitation"),
        PcgAccount("635", "Autres impôts et taxes (CFE, taxes d'apprentissage)", "Charges d'exploitation"),
        PcgAccount("641", "Salaires et traitements du personnel (Rémunérations brutes)", "Charges d'exploitation"),
        PcgAccount("645", "Charges de sécurité sociale et de prévoyance (Charges patronales)", "Charges d'exploitation"),
        PcgAccount("651", "Redevances pour brevets, licences, logiciels", "Charges d'exploitation"),
        PcgAccount("661", "Intérêts des emprunts et dettes", "Charges financières"),
        PcgAccount("671", "Charges exceptionnelles sur opérations de gestion", "Charges exceptionnelles"),
        PcgAccount("6811", "Dotations aux amortissements sur immobilisations", "Charges d'exploitation"),

        // Classe 7
        PcgAccount("701", "Ventes de produits finis", "Produits d'exploitation"),
        PcgAccount("706", "Prestations de services", "Produits d'exploitation"),
        PcgAccount("707", "Ventes de marchandises", "Produits d'exploitation"),
        PcgAccount("708", "Produits des activités annexes", "Produits d'exploitation"),
        PcgAccount("751", "Redevances de propriété industrielle", "Produits d'exploitation"),
        PcgAccount("761", "Produits de participations", "Produits financiers"),
        PcgAccount("762", "Revenus des autres immobilisations financières", "Produits financiers"),
        PcgAccount("771", "Produits exceptionnels sur opérations de gestion", "Produits exceptionnels"),
        PcgAccount("7811", "Reprises sur amortissements et dépréciations", "Produits d'exploitation")
    )

    // Help search either by code or by name
    fun search(query: String): List<PcgAccount> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isBlank()) return accounts.take(5) // default suggestions
        return accounts.filter {
            it.code.contains(trimmed) || it.name.lowercase().contains(trimmed)
        }
    }
}
