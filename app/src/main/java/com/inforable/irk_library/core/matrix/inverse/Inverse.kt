package com.inforable.irk_library.core.matrix.inverse

import com.inforable.irk_library.core.matrix.determinant.*
import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.Locale
import kotlin.math.abs

data class InverseResult(val inverse: Array<DoubleArray>, val steps: List<Step>)
enum class InvMethod { GAUSS_JORDAN, ADJOINT }

private const val EPS = 1e-9
private fun approxZero(x: Double) = kotlin.math.abs(x) < EPS
private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)

// Invers dengan metode OBE, [A||I] -> [I||A^-1]
fun inverseGaussJordan(a: Array<DoubleArray>): InverseResult {
    val n = a.size
    require(n > 0 && a.all { it.size == n }) { "Harus persegi" }

    // Membuat matriks augmented
    val aug = Array(n) { r ->
        DoubleArray(2 * n) { c ->
            if (c < n) a[r][c] else if (c - n == r) 1.0 else 0.0
        }
    }

    val log = StepLogger()

    // Untuk menukar baris
    fun swap(i: Int, j: Int) {
        if (i == j) return
        val t = aug[i]
        aug[i] = aug[j]
        aug[j] = t
        log.add("R${i + 1} ↔ R${j + 1}")
    }

    // Untuk membuat pivot menjadi 1
    fun scale(r: Int, k: Double) {
        for (c in 0 until 2 * n) aug[r][c] /= k
        log.add("R${r + 1} ← (1/${fmt(k)})·R${r + 1}")
    }

    // Untuk operasi baris (Rdst = Rdst - f·Rsrc)
    fun elim(dst: Int, src: Int, f: Double) {
        if (approxZero(f)) return
        for (c in 0 until 2 * n) aug[dst][c] -= f * aug[src][c]
        val sgn = if (f >= 0) "−" else "+"
        val mag = fmt(kotlin.math.abs(f))
        log.add("R${dst + 1} ← R${dst + 1} $sgn $mag·R${src + 1}")
    }

    for (col in 0 until n) {
        var pivot = col
        var best = abs(aug[col][col])
        for (r in col + 1 until n) if (abs(aug[r][col]) > best) {
            best = abs(aug[r][col])
            pivot = r
        }
        if (approxZero(aug[pivot][col])) throw IllegalArgumentException("Matriks singular; tidak punya balikan")
        swap(col, pivot)
        val p = aug[col][col]
        if (!approxZero(p - 1.0)) scale(col, p)
        for (r in 0 until n) if (r != col) elim(r, col, aug[r][col])
    }

    val inv = Array(n) { r -> DoubleArray(n) { c -> aug[r][n + c] } }
    log.add("Hasil: [I | A⁻¹]")

    return InverseResult(inv, log.steps)
}

// Invers dengan metode adjoint
fun inverseAdjoint(a: Array<DoubleArray>): InverseResult {
    val n = a.size
    require(n > 0 && a.all { it.size == n }) { "Harus persegi" }

    val log = StepLogger()
    val deta = determinantOBE(a).determinant // determinant dari a
    if (approxZero(deta)) throw IllegalArgumentException("det(a)=0, maka tidak memiliki balikan")
    log.add("det(a) = ${fmt(deta)} ≠ 0")

    val c = Array(n) { DoubleArray(n) }
    for (i in 0 until n) for (j in 0 until n) {
        val minor = Array(n - 1) { DoubleArray(n - 1) }
        var rr = 0
        for (r in 0 until n) if (r != i) {
            var cc = 0
            for (col in 0 until n) if (col != j) {
                minor[rr][cc] = a[r][col]
                cc++
            }
            rr++
        }
        val minorDet = determinantCofactor(minor).determinant
        val cof = if ((i + j) % 2 == 0) minorDet else -minorDet
        c[i][j] = cof
        log.add("M${i + 1}${j + 1} = det(minor i=${i + 1}, j=${j + 1}) = ${fmt(minorDet)};  C${i + 1}${j + 1} = ${fmt(cof)}")
    }

    val adj = Array(n) { r -> DoubleArray(n) { col -> c[col][r] } }
    log.add("adj(a) = cᵗ")

    val inv = Array(n) { r -> DoubleArray(n) { col -> adj[r][col] / deta } }
    log.add("a⁻¹ = (1/det(a)) · adj(a)")

    return InverseResult(inv, log.steps)
}
