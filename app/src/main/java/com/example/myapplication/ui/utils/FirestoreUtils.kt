package com.example.myapplication.ui.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.models.BagItem

object FirestoreUtils {
    private val db = FirebaseFirestore.getInstance()

    fun fetchBagItems(onSuccess: (List<BagItem>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("bag_list")
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.map { document ->
                    BagItem(
                        name = document.getString("name") ?: "",
                        isChecked = document.getBoolean("isChecked") ?: false
                    )
                }
                onSuccess(items)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addBagItem(item: BagItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val itemMap = hashMapOf(
            "name" to item.name,
            "isChecked" to item.isChecked
        )
        db.collection("bag_list")
            .add(itemMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateBagItem(item: BagItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("bag_list")
            .whereEqualTo("name", item.name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("bag_list").document(document.id)
                        .update("isChecked", item.isChecked)
                }
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun deleteBagItem(item: BagItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("bag_list")
            .whereEqualTo("name", item.name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("bag_list").document(document.id).delete()
                }
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}