package com.inforable.irk_library.core.matrix.spl

import com.inforable.irk_library.core.matrix.format.formatAugmented
import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.Locale
import kotlin.math.abs

data class SplResult(val resultText: String, val steps: List<Step>, val rref: Array<DoubleArray>)

private const val EPS = 1e-9
private fun approxZero(x: Double) = kotlin.math.abs(x) < EPS
private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)

// Gauss–Jordan dengan RREF(Eselon Baris Tereduksi) dan klasifikasi 3 kemungkinan solusi
fun solveSPLGaussJordan(a: Array<DoubleArray>, b: DoubleArray, greedyPivot: Boolean = true): SplResult {
    val n = a.size
    require(n > 0 && a.all { it.size == a[0].size } && b.size == n) { "Dimensi tidak cocok" }

    val m = n
    val aug = Array(m) { i -> DoubleArray(n + 1) { j -> if (j < n) a[i][j] else b[i] } }
    val log = StepLogger()

    fun swap(r1: Int, r2: Int) {
        if (r1 == r2) return
        val t = aug[r1]; aug[r1] = aug[r2]; aug[r2] = t
        log.add("R${r1 + 1} ↔ R${r2 + 1}")
    }

    fun scale(r: Int, k: Double) {
        for (j in 0..n) aug[r][j] /= k
        log.add("R${r + 1} ← (1/${fmt(k)})·R${r + 1}")
    }

    fun elim(dst: Int, src: Int, f: Double) {
        if (approxZero(f)) return
        for (j in 0..n) aug[dst][j] -= f * aug[src][j]
        val sgn = if (f >= 0) "−" else "+"
        val mag = fmt(kotlin.math.abs(f))
        log.add("R${dst + 1} ← R${dst + 1} $sgn $mag·R${src + 1}")
    }

    fun snap(title: String) {
        log.snapshot(title, formatAugmented(aug, n))
    }

    var row = 0
    val pivotCols = mutableListOf<Int>()

    for (col in 0 until n) {
        var pivot = -1
        var best = 0.0
        for (r in row until m) {
            val v = kotlin.math.abs(aug[r][col])
            if (v > EPS && (!greedyPivot || v > best)) {
                best = v
                pivot = r
                if (!greedyPivot) break
            }
        }
        if (pivot == -1) continue
        swap(row, pivot)
        val p = aug[row][col]
        if (!approxZero(p - 1.0)) scale(row, p)
        for (r in 0 until m) if (r != row) elim(r, row, aug[r][col])
        pivotCols += col
        snap("Sesudah pivot kolom ${col + 1}")
        row++
        if (row == m) break
    }

    // cek baris 0…0
    for (i in 0 until m) {
        var allZero = true
        for (j in 0 until n) if (!approxZero(aug[i][j])) { allZero = false; break }
        if (allZero && !approxZero(aug[i][n])) {
            val txt = "Tidak ada solusi (inkonsisten). Ditemukan baris [0 … 0 | ${fmt(aug[i][n])}]"
            return SplResult(txt, log.steps, aug)
        }
    }

    val rank = pivotCols.size
    if (rank == n) {
        val x = DoubleArray(n)
        for ((r, c) in pivotCols.withIndex()) x[c] = aug[r][n]
        val txt = buildString {
            appendLine("Solusi unik:")
            for (i in 0 until n) appendLine("x${i + 1} = ${fmt(x[i])}")
        }
        return SplResult(txt.trimEnd(), log.steps, aug)
    } else {
        val freeCols = (0 until n).filterNot { it in pivotCols }
        val names = listOf("s","t","u","v","w","r","k","p","q")
        val nameByCol = freeCols.mapIndexed { idx, c -> c to names[idx % names.size] }.toMap()

        val lines = mutableListOf<String>()
        lines += "Solusi banyak (tak berhingga). Misalkan:"
        freeCols.forEach { c -> lines += "x${c + 1} = ${nameByCol[c]}" }
        for ((r, c) in pivotCols.withIndex()) {
            val rhs = aug[r][n]
            val terms = mutableListOf<String>()
            for (j in freeCols) {
                val coef = aug[r][j]
                if (!approxZero(coef)) terms += "${fmt(-coef)}·${nameByCol[j]}"
            }
            lines += "x${c + 1} = " + (listOf(fmt(rhs)) + terms).joinToString(" + ")
        }
        return SplResult(lines.joinToString("\n"), log.steps, aug)
    }
}