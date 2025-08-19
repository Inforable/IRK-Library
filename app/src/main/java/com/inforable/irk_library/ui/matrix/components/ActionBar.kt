package com.inforable.irk_library.ui.matrix.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionBar(
    canShowSteps:Boolean,
    showSteps:Boolean,
    onSolve:()->Unit,
    onToggleSteps:()->Unit
){
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onSolve, modifier = Modifier.weight(1f)) { Text("Solve") }
        OutlinedButton(onClick = onToggleSteps, enabled = canShowSteps, modifier = Modifier.weight(1f)) {
            Text(if (showSteps) "Hide Steps" else "Show Solution")
        }
    }
}
