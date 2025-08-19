package com.inforable.irk_library.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val DEV_NAME = "Hasri Fayadh Muqaffa"
private const val DEV_EMAIL = "hasri.fayadh@gmail.com"
private const val DEV_LINE = "hasrifayadhmuqaffa"
private const val DEV_MOTIVATION = "Motivasi membuat aplikasi ini adalah karena ingin mempermudah orang-orang untuk belajar menemukan solusi dari permasalahan SPL, Kripto dan Huffman."
private const val DEV_GOAL = "Ingin menjadi asisten yang gacor."

@Composable
fun AboutScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("About", style = MaterialTheme.typography.titleLarge)
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Pembuat Aplikasi", style = MaterialTheme.typography.titleMedium)
                Text(DEV_NAME)
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Motivasi", style = MaterialTheme.typography.titleMedium)
                Text(DEV_MOTIVATION)
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Harapan sebagai asisten IRK", style = MaterialTheme.typography.titleMedium)
                Text(DEV_GOAL)
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Kontak", style = MaterialTheme.typography.titleMedium)
                Text("Email: $DEV_EMAIL")
                Text("LINE: $DEV_LINE")
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Terima kasih sudah mencoba IRK Library!",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}