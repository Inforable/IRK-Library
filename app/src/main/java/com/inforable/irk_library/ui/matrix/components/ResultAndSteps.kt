package com.inforable.irk_library.ui.matrix.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inforable.irk_library.core.matrix.steps.Step

@Composable
fun ResultAndSteps(
    resultText:String,
    error:String?,
    steps: List<Step>,
    showSteps:Boolean
){
    if (error != null) Text("Error: $error", color = MaterialTheme.colorScheme.error)
    if (resultText.isNotBlank()) {
        Text("Result", style = MaterialTheme.typography.titleMedium)
        Text(resultText, style = MaterialTheme.typography.bodyLarge)
    }
    if (showSteps && steps.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Text("Steps", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            itemsIndexed(steps) { idx, s ->
                Text("${idx+1}. ${s.text}")
            }
        }
    }
}