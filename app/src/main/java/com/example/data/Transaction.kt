package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

data class Accompaniment(
    val label: String,
    val isDebit: Boolean,
    val account: String,
    val amount: Double
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // format "YYYY-MM-DD"
    val libelle: String,
    val debitAccount: String,
    val debitAmount: Double,
    val creditAccount: String,
    val creditAmount: Double,
    val accompanimentsJson: String = "[]"
) {
    // Utility to get list of accompaniments
    fun getAccompaniments(): List<Accompaniment> {
        val list = mutableListOf<Accompaniment>()
        if (accompanimentsJson.isEmpty()) return list
        try {
            val array = JSONArray(accompanimentsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Accompaniment(
                        label = obj.optString("label", ""),
                        isDebit = obj.optBoolean("isDebit", true),
                        account = obj.optString("account", ""),
                        amount = obj.optDouble("amount", 0.0)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    companion object {
        fun convertAccompanimentsToJson(list: List<Accompaniment>): String {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("label", item.label)
                obj.put("isDebit", item.isDebit)
                obj.put("account", item.account)
                obj.put("amount", item.amount)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
