package com.inforable.irk_library.ui.huffman

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuffmanScreen(vm: HuffmanViewModel = viewModel()) {
    val st = vm.state
    Scaffold(topBar = { TopAppBar(title = { Text("Huffman") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = st.input, onValueChange = vm::setInput,
                label = { Text("Input teks (contoh: ABACCDA)") }, modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::build) { Text("Build") }
                OutlinedButton(onClick = vm::toggleBuildSteps, enabled = st.stepsBuild.isNotEmpty()) {
                    Text(if (st.showBuildSteps) "Hide Steps" else "Show Steps")
                }
            }
            if (st.error != null) Text("Error: ${st.error}", color = MaterialTheme.colorScheme.error)

            if (st.built) {
                Text("Build steps: ${st.progress}/${st.mergesCount}")
                Slider(
                    value = st.progress.toFloat(),
                    onValueChange = { vm.setProgress(it.toInt()) },
                    valueRange = 0f..st.mergesCount.toFloat(),
                    steps = (if (st.mergesCount > 0) st.mergesCount - 1 else 0),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tabel frekuensi
                Text("Frekuensi:", style = MaterialTheme.typography.titleSmall)
                Text(st.freqTable, fontFamily = FontFamily.Monospace)

                // ASCII tree
                Text("Tree (kiri=0, kanan=1):", style = MaterialTheme.typography.titleSmall)
                Card { Text(st.asciiTree, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(12.dp)) }

                if (st.codes.isNotEmpty()) {
                    Text("Kode Huffman:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        st.codes.entries.sortedBy { it.key }
                            .joinToString("   ") { "${it.key}=${it.value}" },
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (st.showBuildSteps && st.stepsBuild.isNotEmpty()) {
                Text("Langkah pembentukan:", style = MaterialTheme.typography.titleSmall)
                st.stepsBuild.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            // Encode
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::encode, enabled = st.built) { Text("Encode") }
                OutlinedButton(onClick = vm::toggleEncSteps, enabled = st.stepsEnc.isNotEmpty()){
                    Text(if (st.showEncSteps) "Hide Steps" else "Show Steps")
                }
            }
            if (st.bitOut.isNotBlank()) {
                Text("Cipher bits:", style = MaterialTheme.typography.titleSmall)
                Text(st.bitOut, fontFamily = FontFamily.Monospace)
            }
            if (st.showEncSteps && st.stepsEnc.isNotEmpty()) {
                Text("Langkah encoding:", style = MaterialTheme.typography.titleSmall)
                st.stepsEnc.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
            }

            // Decode
            OutlinedTextField(
                value = st.decBits, onValueChange = vm::setDecBits,
                label = { Text("Input bits untuk decode") }, modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::decode, enabled = st.built) { Text("Decode") }
                OutlinedButton(onClick = vm::toggleDecSteps, enabled = st.stepsDec.isNotEmpty()){
                    Text(if (st.showDecSteps) "Hide Steps" else "Show Steps")
                }
            }
            if (st.plainOut.isNotBlank()) {
                Text("Plaintext:", style = MaterialTheme.typography.titleSmall)
                Text(st.plainOut, fontFamily = FontFamily.Monospace)
            }
            if (st.showDecSteps && st.stepsDec.isNotEmpty()) {
                Text("Langkah decoding:", style = MaterialTheme.typography.titleSmall)
                st.stepsDec.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
            }
        }
    }
}
