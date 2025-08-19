package com.inforable.irk_library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.inforable.irk_library.ui.crypto.CryptoScreen
import com.inforable.irk_library.ui.huffman.HuffmanScreen
import com.inforable.irk_library.ui.matrix.MatrixScreen
import com.inforable.irk_library.ui.about.AboutScreen
import com.inforable.irk_library.ui.theme.IRKLibraryTheme

sealed class Screen(val route: String, val label: String) {
    data object Matrix : Screen("matrix","Matrix")
    data object Crypto : Screen("crypto","Crypto")
    data object Huffman : Screen("huffman","Huffman")
    data object About : Screen("about","About")
}

@Composable
fun IRKApp() {
    val nav = rememberNavController()
    val items = listOf(Screen.Matrix, Screen.Crypto, Screen.Huffman, Screen.About)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { s ->
                    NavigationBarItem(
                        selected = current == s.route,
                        onClick = {
                            nav.navigate(s.route) {
                                launchSingleTop = true
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                                restoreState = true
                            }
                        },
                        icon = { Text(s.label.first().toString()) },
                        label = { Text(s.label) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = Screen.Matrix.route, modifier = Modifier.padding(pad)) {
            composable(Screen.Matrix.route) { MatrixScreen() }
            composable(Screen.Crypto.route) { CryptoScreen() }
            composable(Screen.Huffman.route) { HuffmanScreen() }
            composable(Screen.About.route) { AboutScreen() }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { IRKLibraryTheme { IRKApp() } }
    }
}