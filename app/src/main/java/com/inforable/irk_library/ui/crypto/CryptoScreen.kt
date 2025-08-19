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
    
    Scaffold(
        topBar = { 
            TopAppBar(title = { Text("Cryptography (Lite)") }) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add top spacer
            Spacer(modifier = Modifier.height(8.dp))
            
            // ===== Caesar Card =====
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Caesar 26 huruf", 
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = st.caesar.shift, 
                        onValueChange = vm::setCaesarShift, 
                        label = { Text("Shift k") }, 
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = st.caesar.input, 
                        onValueChange = vm::setCaesarInput, 
                        label = { Text("Input") }, 
                        modifier = Modifier.fillMaxWidth(), 
                        minLines = 2,
                        maxLines = 4
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = vm::caesarEncrypt,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text("Encrypt") 
                        }
                        OutlinedButton(
                            onClick = vm::caesarDecrypt,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text("Decrypt") 
                        }
                    }
                    
                    if (st.caesar.steps.isNotEmpty()) {
                        OutlinedButton(
                            onClick = vm::toggleCaesarSteps,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (st.caesar.showSteps) "Hide Steps" else "Show Steps")
                        }
                    }
                    
                    if (st.caesar.error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                "Error: ${st.caesar.error}", 
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    if (st.caesar.output.isNotBlank()) {
                        Text("Output", style = MaterialTheme.typography.titleSmall)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                st.caesar.output,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    if (st.caesar.showSteps && st.caesar.steps.isNotEmpty()) {
                        Text("Langkah (sesuai PPT):", style = MaterialTheme.typography.titleSmall)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                st.caesar.steps.forEachIndexed { i, s -> 
                                    Text(
                                        "${i+1}. ${s.text}",
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) 
                                }
                            }
                        }
                    }
                }
            }

            // ===== RSA Card =====
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "RSA (keygen → encrypt → decrypt)", 
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Key Generation Section
                    Text(
                        "1. Key Generation",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = st.rsa.p, 
                            onValueChange = vm::setP, 
                            label = { Text("p (prima)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = st.rsa.q, 
                            onValueChange = vm::setQ, 
                            label = { Text("q (prima)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = st.rsa.e, 
                            onValueChange = vm::setE, 
                            label = { Text("e") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = vm::generateKeys,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text("Generate Keys") 
                        }
                        if (st.rsa.keySteps.isNotEmpty()) {
                            OutlinedButton(
                                onClick = vm::toggleKeySteps,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (st.rsa.showKeySteps) "Hide Steps" else "Show Steps")
                            }
                        }
                    }
                    
                    if (st.rsa.n.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Generated Keys:", style = MaterialTheme.typography.titleSmall)
                                Text("n = ${st.rsa.n}")
                                Text("φ(n) = ${st.rsa.phi}")
                                Text("d = ${st.rsa.d}")
                            }
                        }
                    }
                    
                    if (st.rsa.showKeySteps && st.rsa.keySteps.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Langkah Keygen:", style = MaterialTheme.typography.titleSmall)
                                st.rsa.keySteps.forEachIndexed { i, s -> 
                                    Text(
                                        "${i+1}. ${s.text}",
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) 
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Encryption Section
                    Text(
                        "2. Encryption",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = st.rsa.message, 
                        onValueChange = vm::setMessage, 
                        label = { Text("Plaintext (A..Z & spasi)") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = st.rsa.blockSize, 
                        onValueChange = vm::setBlockSize, 
                        label = { Text("Block size (digit, default 3)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = vm::encrypt,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text("Encrypt") 
                        }
                        if (st.rsa.encSteps.isNotEmpty()) {
                            OutlinedButton(
                                onClick = vm::toggleEncSteps,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (st.rsa.showEncSteps) "Hide Steps" else "Show Steps")
                            }
                        }
                    }
                    
                    if (st.rsa.cipherBlocks.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Cipher blocks:", style = MaterialTheme.typography.titleSmall)
                                Text(st.rsa.cipherBlocks)
                            }
                        }
                    }
                    
                    if (st.rsa.showEncSteps && st.rsa.encSteps.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Langkah Enkripsi:", style = MaterialTheme.typography.titleSmall)
                                st.rsa.encSteps.forEachIndexed { i, s -> 
                                    Text(
                                        "${i+1}. ${s.text}",
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) 
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Decryption Section
                    Text(
                        "3. Decryption",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = st.rsa.cipherBlocks,
                        onValueChange = vm::setDecInput,
                        label = { Text("Input cipher blocks (spasi)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = vm::decrypt,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text("Decrypt") 
                        }
                        if (st.rsa.decSteps.isNotEmpty()) {
                            OutlinedButton(
                                onClick = vm::toggleDecSteps,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (st.rsa.showDecSteps) "Hide Steps" else "Show Steps")
                            }
                        }
                    }
                    
                    if (st.rsa.plainOut.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Plaintext:", style = MaterialTheme.typography.titleSmall)
                                Text(st.rsa.plainOut)
                            }
                        }
                    }
                    
                    if (st.rsa.error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                "Error: ${st.rsa.error}", 
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    if (st.rsa.showDecSteps && st.rsa.decSteps.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Langkah Dekripsi:", style = MaterialTheme.typography.titleSmall)
                                st.rsa.decSteps.forEachIndexed { i, s -> 
                                    Text(
                                        "${i+1}. ${s.text}",
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) 
                                }
                            }
                        }
                    }
                }
            }
            
            // Add bottom spacer to prevent bottom nav overlap
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}