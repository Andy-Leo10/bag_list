package com.example.myapplication

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
import androidx.compose.material3.Checkbox
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

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
    var items = remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var text by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter item") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { 
            if (text.isNotBlank()) {
                items.value = items.value + Pair(text, false)
                text = "" // Clear the text field after adding
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
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked, 
                        onCheckedChange = { checked ->
                            items.value = items.value.map {
                                if (it.first == item) item to checked else it
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