package com.example.myapplication
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource

import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.components.BagList
import com.example.myapplication.ui.components.Counter
import com.example.myapplication.ui.components.Title
import com.example.myapplication.ui.components.Print

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
fun MainApplicationContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Title(stringResource(id = R.string.title))
        }
        // Print(stringResource(id = R.string.text_of_example))
        BagList()
        // Counter()
    }
}