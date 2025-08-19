package com.inforable.irk_library.ui.crypto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoScreen(vm: CryptoViewModel = viewModel()) {
    val st = vm.state
    Scaffold(topBar = { TopAppBar(title = { Text("Cryptography") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Caesar
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Caesar 26 huruf", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = st.caesar.shift, onValueChange = vm::setCaesarShift, label = { Text("Shift k") }, singleLine = true)
                    OutlinedTextField(value = st.caesar.input, onValueChange = vm::setCaesarInput, label = { Text("Input") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = vm::caesarEncrypt) { Text("Encrypt") }
                        OutlinedButton(onClick = vm::caesarDecrypt) { Text("Decrypt") }
                        OutlinedButton(onClick = vm::toggleCaesarSteps, enabled = st.caesar.steps.isNotEmpty()) {
                            Text(if (st.caesar.showSteps) "Hide Steps" else "Show Steps")
                        }
                    }
                    if (st.caesar.error != null) Text("Error: ${st.caesar.error}", color = MaterialTheme.colorScheme.error)
                    if (st.caesar.output.isNotBlank()) {
                        Text("Output", style = MaterialTheme.typography.titleSmall)
                        Text(st.caesar.output)
                    }
                    if (st.caesar.showSteps && st.caesar.steps.isNotEmpty()) {
                        Text("Langkah:", style = MaterialTheme.typography.titleSmall)
                        st.caesar.steps.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
                    }
                }
            }

            // ===== RSA =====
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("RSA (keygen -> encrypt -> decrypt)", style = MaterialTheme.typography.titleMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = st.rsa.p, onValueChange = vm::setP, label = { Text("p (prima)") })
                        OutlinedTextField(value = st.rsa.q, onValueChange = vm::setQ, label = { Text("q (prima)") })
                        OutlinedTextField(value = st.rsa.e, onValueChange = vm::setE, label = { Text("e, gcd(e,φ)=1") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = vm::generateKeys) { Text("Generate Keys") }
                        OutlinedButton(onClick = vm::toggleKeySteps, enabled = st.rsa.keySteps.isNotEmpty()) {
                            Text(if (st.rsa.showKeySteps) "Hide Steps" else "Show Steps")
                        }
                    }
                    if (st.rsa.n.isNotBlank()) {
                        Text("n = ${st.rsa.n}")
                        Text("φ(n) = ${st.rsa.phi}")
                        Text("d = ${st.rsa.d}")
                    }
                    if (st.rsa.showKeySteps && st.rsa.keySteps.isNotEmpty()) {
                        Text("Langkah Keygen:", style = MaterialTheme.typography.titleSmall)
                        st.rsa.keySteps.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
                    }

                    Divider(Modifier.padding(vertical = 8.dp))

                    OutlinedTextField(value = st.rsa.message, onValueChange = vm::setMessage, label = { Text("Plaintext (A..Z & spasi)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = st.rsa.blockSize, onValueChange = vm::setBlockSize, label = { Text("Block size (digit, default 3)") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = vm::encrypt) { Text("Encrypt") }
                        OutlinedButton(onClick = vm::toggleEncSteps, enabled = st.rsa.encSteps.isNotEmpty()) {
                            Text(if (st.rsa.showEncSteps) "Hide Steps" else "Show Steps")
                        }
                    }
                    if (st.rsa.cipherBlocks.isNotBlank()) {
                        Text("Cipher blocks:")
                        Text(st.rsa.cipherBlocks)
                    }
                    if (st.rsa.showEncSteps && st.rsa.encSteps.isNotEmpty()) {
                        Text("Langkah Enkripsi:", style = MaterialTheme.typography.titleSmall)
                        st.rsa.encSteps.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
                    }

                    Divider(Modifier.padding(vertical = 8.dp))

                    OutlinedTextField(
                        value = st.rsa.cipherBlocks,
                        onValueChange = vm::setDecInput,
                        label = { Text("Input cipher blocks (spasi)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = vm::decrypt) { Text("Decrypt") }
                        OutlinedButton(onClick = vm::toggleDecSteps, enabled = st.rsa.decSteps.isNotEmpty()) {
                            Text(if (st.rsa.showDecSteps) "Hide Steps" else "Show Steps")
                        }
                    }
                    if (st.rsa.plainOut.isNotBlank()) {
                        Text("Plaintext:", style = MaterialTheme.typography.titleSmall)
                        Text(st.rsa.plainOut)
                    }
                    if (st.rsa.error != null) Text("Error: ${st.rsa.error}", color = MaterialTheme.colorScheme.error)
                    if (st.rsa.showDecSteps && st.rsa.decSteps.isNotEmpty()) {
                        Text("Langkah Dekripsi:", style = MaterialTheme.typography.titleSmall)
                        st.rsa.decSteps.forEachIndexed { i, s -> Text("${i+1}. ${s.text}") }
                    }
                }
            }
        }
    }
}
