package com.inforable.irk_library.ui.matrix.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inforable.irk_library.core.matrix.determinant.DetMethod
import com.inforable.irk_library.core.matrix.inverse.InvMethod

@Composable
fun DetMethodSelector(selected: DetMethod, onSelect:(DetMethod)->Unit){
    Row {
        FilterChip(selected==DetMethod.OBE, { onSelect(DetMethod.OBE) }, label={ Text("Row Ops") })
        Spacer(Modifier.width(8.dp))
        FilterChip(selected==DetMethod.COFACTOR, { onSelect(DetMethod.COFACTOR) }, label={ Text("Cofactor") })
        Spacer(Modifier.width(8.dp))
        FilterChip(selected==DetMethod.SARRUS3, { onSelect(DetMethod.SARRUS3) }, label={ Text("Sarrus 3×3") })
    }
}

@Composable
fun InvMethodSelector(selected: InvMethod, onSelect:(InvMethod)->Unit){
    Row {
        FilterChip(selected==InvMethod.GAUSS_JORDAN, { onSelect(InvMethod.GAUSS_JORDAN) }, label={ Text("Gauss–Jordan") })
        Spacer(Modifier.width(8.dp))
        FilterChip(selected==InvMethod.ADJOINT, { onSelect(InvMethod.ADJOINT) }, label={ Text("Adjoint") })
    }
}
