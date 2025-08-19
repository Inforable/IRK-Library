package com.inforable.irk_library.core.matrix.format

import java.util.Locale
// Formatting untuk nilai dibelakang koma
private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)

// Format matriks biasa
fun formatMatrix(m: Array<DoubleArray>): String = buildString {
    m.forEach { row ->
        append(row.joinToString(prefix = "[ ", postfix = " ]") { fmt(it) })
        append('\n')
    }
}

// Format matriks augmented
fun formatAugmented(aug: Array<DoubleArray>, splitCol: Int): String = buildString {
    val n = aug.size
    val m = aug[0].size
    for (i in 0 until n) {
        append("[ ")
        for (j in 0 until m) {
            if (j == splitCol) append("| ")
            append(fmt(aug[i][j]))
            if (j != m - 1) append(" ")
        }
        append(" ]\n")
    }
}