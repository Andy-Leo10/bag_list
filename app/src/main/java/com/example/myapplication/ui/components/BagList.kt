// filepath: c:\Users\Andres.Alamo\Documents\Proyectos\bag_list\app\src\main\java\com\example\myapplication\ui\components\BagList.kt
package com.example.myapplication.ui.components
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.Checkbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.LaunchedEffect

@Composable
fun BagList() {
    val db = FirebaseFirestore.getInstance()
    var items = remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("bag_list")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedItems = documents.map { document ->
                    Pair(
                        document.getString("name") ?: "",
                        document.getBoolean("isChecked") ?: false
                    )
                }
                items.value = fetchedItems
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(id = R.string.enter_item_label)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { 
            if (text.isNotBlank()) {
                val newItem = Pair(text, false)
                items.value = items.value + newItem
                text = "" // Clear the text field after adding

                // Save to Firestore
                val itemMap = hashMapOf(
                    "name" to newItem.first,
                    "isChecked" to newItem.second
                )
                db.collection("bag_list")
                    .add(itemMap)
                    .addOnSuccessListener { 
                        // Successfully added
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                    }
            }
        }) {
            Text(stringResource(id = R.string.add_to_bag_list))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items.value ?: emptyList()) { (item, isChecked) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked, 
                        onCheckedChange = { checked ->
                            items.value = items.value.map {
                                if (it.first == item) item to checked else it
                            }

                            // Update Firestore
                            db.collection("bag_list")
                                .whereEqualTo("name", item)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        db.collection("bag_list").document(document.id)
                                            .update("isChecked", checked)
                                    }
                                }
                        }
                    )
                    Text(
                        text = item,
                        style = if (isChecked) {
                            TextStyle(textDecoration = TextDecoration.LineThrough)
                        } else {
                            TextStyle(fontWeight = FontWeight.Bold)
                        }
                    )
                    IconButton(onClick = {
                        items.value = items.value.filterNot { it.first == item }

                        // Delete from Firestore
                        db.collection("bag_list")
                            .whereEqualTo("name", item)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    db.collection("bag_list").document(document.id).delete()
                                }
                            }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete item"
                        )
                    }
                }
            }
        }
    }
}