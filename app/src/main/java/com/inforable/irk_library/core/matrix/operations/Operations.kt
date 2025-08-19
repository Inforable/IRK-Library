package com.inforable.irk_library.core.matrix.operations

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.Locale

data class MatrixOpResult(val result: Array<DoubleArray>, val steps: List<Step>)
data class ScalarOpResult(val value: Double, val steps: List<Step>)

private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)
private fun rows(a: Array<DoubleArray>) = a.size
private fun cols(a: Array<DoubleArray>) = if (a.isNotEmpty()) a[0].size else 0
private fun sameSize(a: Array<DoubleArray>, b: Array<DoubleArray>) = rows(a) == rows(b) && cols(a) == cols(b)

// Membuat matriks nol berukuran r x c
fun zeroMatrix(r: Int, c: Int) = Array(r) {DoubleArray(c) }
// Membuat matriks identitas berukuran n x n
fun identity(n: Int): Array<DoubleArray> = Array(n) { i -> DoubleArray(n) {j -> if (i == j) 1.0 else 0.0} }

// Menambahkan dua matriks
fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): MatrixOpResult {
    require(sameSize(a,b)) { "Dimensi kedua matrix harus sama" }
    val r = rows(a)
    val c = cols(a)
    val result = zeroMatrix(r, c)
    val log = StepLogger()
    for (i in 0 until r) for (j in 0 until c) {
        result[i][j] = a[i][j] + b[i][j]
        log.add("c[${i+1},${j+1}] = a[${i+1},${j+1}] + b[${i+1},${j+1}] = ${fmt(a[i][j])} + ${fmt(b[i][j])} = ${fmt(result[i][j])}")
    }
    return MatrixOpResult(result, log.steps)
}

// Mengurangi dua matriks
fun sub(a: Array<DoubleArray>, b: Array<DoubleArray>): MatrixOpResult {
    require(sameSize(a,b)) { "Dimensi kedua matrix harus sama" }
    val r = rows(a)
    val c = cols(a)
    val result = zeroMatrix(r, c)
    val log = StepLogger()
    for (i in 0 until r) for (j in 0 until c) {
        result[i][j] = a[i][j] - b[i][j]
        log.add("c[${i+1},${j+1}] = a[${i+1},${j+1}] - b[${i+1},${j+1}] = ${fmt(a[i][j])} - ${fmt(b[i][j])} = ${fmt(result[i][j])}")
    }
    return MatrixOpResult(result, log.steps)
}

// Perkalian skalar
fun scalarMultiply(a: Array<DoubleArray>, x: Double): MatrixOpResult {
    val r = rows(a)
    val c = cols(a)
    var result = zeroMatrix(r, c)
    val log = StepLogger()
    for (i in 0 until r) for (j in 0 until c) {
        result[i][j] = a[i][j] * x
        log.add("c[${i+1},${j+1}] = a[${i+1},${j+1}] * $x = ${fmt(a[i][j])} * $x = ${fmt(result[i][j])}")
    }
    return MatrixOpResult(result, log.steps)
}

// Transpose Matriks
fun transpose(a: Array<DoubleArray>): MatrixOpResult {
    val r = rows(a)
    val c = cols(a)
    val result = zeroMatrix(c, r)
    val log = StepLogger()
    for (i in 0 until r) for (j in 0 until c) {
        result[j][i] = a[i][j]
        log.add("c[${j+1},${i+1}] = a[${i+1},${j+1}] = ${fmt(a[i][j])}")
    }
    return MatrixOpResult(result, log.steps)
}

// Trace Matriks (jumlah nilai dari diagonal utama)
fun trace(a: Array<DoubleArray>): ScalarOpResult {
    require(rows(a) == cols(a)) { "Trace hanya bisa untuk matriks persegi" }
    val n = rows(a)
    val log = StepLogger()
    var result = 0.0
    val pieces = mutableListOf<String>()
    for (i in 0 until n) {
        result += a[i][i]
        pieces += "a[${i+1},${i+1}]=${fmt(a[i][i])}"
        log.add("tr(a) = " + pieces.joinToString(" + ") + " = ${fmt(result)}")
    }
    return ScalarOpResult(result, log.steps)
}

fun matrixMultiply(a: Array<DoubleArray>, b: Array<DoubleArray>): MatrixOpResult {
    require(cols(a) == rows(b)) { "Dimensi tidak cocok" }
    val m = rows(a)
    val n = cols(a)
    val p = cols(b)
    val result = zeroMatrix(m, p)
    val log = StepLogger()
    for (i in 0 until m) for (j in 0 until p) {
        var tmp = 0.0
        var terms = mutableListOf<String>()
        for (k in 0 until n) {
            tmp += a[i][k] * b[k][j]
            terms += "a[${i+1},${k+1}] * b[${k+1},${j+1}]"
        }
        result[i][j] = tmp
        log.add("c[${i+1},${j+1}] = " + terms.joinToString(" + ") + " = ${fmt(tmp)}")
    }
    return MatrixOpResult(result, log.steps)
}