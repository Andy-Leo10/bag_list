package com.example.myapplication.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = typography.headlineMedium,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}