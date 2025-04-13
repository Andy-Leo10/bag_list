package com.example.myapplication
import com.google.firebase.firestore.FirebaseFirestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.material3.Checkbox
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApplicationContent()
            }
        }
    }
}

@Composable
fun MainApplicationContent(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adds spacing between items
        ) {
            Title(title = "My Bag List", modifier = Modifier.fillMaxWidth())
            BagList()
        }
    }
}

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = typography.headlineMedium,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun Print(content: String, modifier: Modifier = Modifier) {
    Text(
        text = content,
        modifier = modifier
    )
}

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
            label = { Text("Enter item") }
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
            Text(text = "Add to Bag List")
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

@Composable
fun Counter() {
    // Declare a state variable using remember and mutableStateOf
    var count by remember { mutableStateOf(0) }

    // UI that reacts to state changes
    Column {
        Text(text = "Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainApplicationPreview() {
    MyApplicationTheme {
        MainApplicationContent()
    }
}