package com.inforable.irk_library.ui.matrix.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MatrixGrid(
    n:Int,
    values: List<MutableList<String>>,
    onChange:(r:Int,c:Int,v:String)->Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        repeat(n){ r->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(n){ c->
                    val v = values[r][c]
                    OutlinedTextField(
                        value = v,
                        onValueChange = { s ->
                            if (s.isEmpty() || s.matches(Regex("^-?\\d*\\.?\\d*$"))) onChange(r,c,s)
                        },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        label = { Text("a${r+1}${c+1}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        }
    }
}

@Composable
fun VectorInput(
    n:Int,
    values: List<String>,
    onChange:(i:Int,v:String)->Unit
){
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(n){ i->
            OutlinedTextField(
                value = values[i],
                onValueChange = { s ->
                    if (s.isEmpty() || s.matches(Regex("^-?\\d*\\.?\\d*$"))) onChange(i,s)
                },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                label = { Text("b${i+1}") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}