package com.inforable.irk_library.ui.matrix

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inforable.irk_library.core.matrix.determinant.DetMethod
import com.inforable.irk_library.core.matrix.inverse.InvMethod
import com.inforable.irk_library.ui.matrix.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(vm: MatrixViewModel = viewModel()) {
    val st = vm.state

    Scaffold(topBar = {
        TopAppBar(title = { Text("Matrix") })
    }) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // ← Add scroll
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Operation selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(st.op==Operation.SPL, onClick={ vm.setOp(Operation.SPL) }, label={ Text("SPL") })
                FilterChip(st.op==Operation.DETERMINANT, onClick={ vm.setOp(Operation.DETERMINANT) }, label={ Text("Determinant") })
                FilterChip(st.op==Operation.INVERSE, onClick={ vm.setOp(Operation.INVERSE) }, label={ Text("Inverse") })
                FilterChip(st.op==Operation.CRAMER, onClick={ vm.setOp(Operation.CRAMER) }, label={ Text("Cramer") })
            }

            // Size & options - FIX: Use FilterChip instead of AssistChip
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = st.n == 2,
                    onClick = { vm.setSize(2) },
                    label = { Text("2×2") }
                )
                FilterChip(
                    selected = st.n == 3,
                    onClick = { vm.setSize(3) },
                    label = { Text("3×3") }
                )
                FilterChip(
                    selected = st.n == 4,
                    onClick = { vm.setSize(4) },
                    label = { Text("4×4") }
                )
                if (st.op!=Operation.DETERMINANT) {
                    FilterChip(st.pivotGreedy, onClick={ vm.setPivotGreedy(!st.pivotGreedy) }, label={ Text("Greedy Pivot") })
                }
            }

            // Input grids
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Matrix A", style = MaterialTheme.typography.titleMedium)
                    MatrixGrid(st.n, st.A, vm::setA)

                    if (st.op==Operation.SPL || st.op==Operation.CRAMER) {
                        Spacer(Modifier.height(8.dp))
                        Text("Vector b", style = MaterialTheme.typography.titleMedium)
                        VectorInput(st.n, st.b, vm::setB)
                    }
                }
            }

            // Method selectors per op
            when (st.op) {
                Operation.DETERMINANT, Operation.CRAMER -> {
                    DetMethodSelector(st.detMethod) { m -> vm.setDetMethod(m) }
                }
                Operation.INVERSE -> {
                    InvMethodSelector(st.invMethod) { m -> vm.setInvMethod(m) }
                }
                else -> {}
            }

            // Action bar
            ActionBar(
                canShowSteps = st.steps.isNotEmpty(),
                showSteps = st.showSteps,
                onSolve = vm::solve,
                onToggleSteps = { vm.setShowSteps(!st.showSteps) }
            )

            // Output
            ResultAndSteps(
                resultText = st.resultText,
                error = st.error,
                steps = st.steps,
                showSteps = st.showSteps
            )
        }
    }
}