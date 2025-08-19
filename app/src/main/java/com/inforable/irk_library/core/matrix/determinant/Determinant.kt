package com.inforable.irk_library.core.matrix.determinant

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.Locale
import kotlin.math.abs

data class DetResult(val determinant: Double, val steps: List<Step>)
enum class DetMethod { OBE, COFACTOR, SARRUS3 }

private const val EPS = 1e-9
private fun approxZero(x: Double) = kotlin.math.abs(x) < EPS
private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)

// Determinan dengan metode OBE
fun determinantOBE(a: Array<DoubleArray>): DetResult {
    val n = a.size
    require(n > 0 && a.all { it.size == n }) { "Harus persegi" }

    val m = Array(n) { i -> a[i].clone() }
    val log = StepLogger()
    var swaps = 0
    var scaleProd = 1.0

    // Untuk menukar baris
    fun swap(i: Int, j: Int) {
        if (i == j) return
        val t = m[i]
        m[i] = m[j]
        m[j] = t
        swaps++
        log.add("R${i + 1} ↔ R${j + 1}  (det berubah tanda)")
    }

    // Untuk operasi baris (Rdst = Rdst - k·Rsrc)
    fun elim(dst: Int, src: Int, k: Double) {
        if (approxZero(k)) return
        for (c in 0 until n) m[dst][c] -= k * m[src][c]
        val sgn = if (k >= 0) "−" else "+"
        val mag = fmt(kotlin.math.abs(k))
        log.add("R${dst + 1} ← R${dst + 1} $sgn $mag·R${src + 1}  (det tidak berubah)")
    }

    // Mncari pivot terbesar di kolom
    for (col in 0 until n) {
        var pivot = col
        var best = abs(m[col][col])
        for (r in col + 1 until n) {
            if (abs(m[r][col]) > best) {
                best = abs(m[r][col])
                pivot = r
            }
        }
        if (approxZero(m[pivot][col])) {
            return DetResult(0.0, log.steps + Step("Pivot kolom ${col + 1} ≈ 0 → det(A)=0"))
        }
        swap(col, pivot)
        for (r in col + 1 until n) {
            val k = m[r][col] / m[col][col]
            elim(r, col, k)
        }
    }

    var diagonal = 1.0
    for (i in 0 until n) diagonal *= m[i][i]

    val det = (if (swaps % 2 == 0) +1 else -1) * diagonal * (1.0 / scaleProd)
    log.add("det(A) = (-1)^$swaps × (∏ diagonal) × (1/${fmt(scaleProd)}) = ${fmt(det)}")
    return DetResult(det, log.steps)
}

// Determinan dengan metode kofaktor
fun determinantCofactor(a: Array<DoubleArray>): DetResult {
    val n = a.size
    require(n > 0 && a.all { it.size == n }) { "Harus persegi" }
    val log = StepLogger()

    fun detRecursive(m: Array<DoubleArray>, depth: Int, label: String): Double {
        val nn = m.size

        // Base case jika ukuran matriks 1x1
        if (nn == 1) {
            log.add("${"  ".repeat(depth)}det($label) = ${fmt(m[0][0])}")
            return m[0][0]
        }

        // Base case jika ukuran matriks 2x2
        if (nn == 2) {
            val d = m[0][0] * m[1][1] - m[0][1] * m[1][0]
            log.add("${"  ".repeat(depth)}det($label) = a11·a22 − a12·a21 = ${fmt(d)}")
            return d
        }

        var sum = 0.0
        log.add("${"  ".repeat(depth)}Ekspansi kofaktor baris 1: det($label) = Σ a1j·C1j")

        for (j in 0 until nn) {
            val minor = Array(nn - 1) { DoubleArray(nn - 1) }
            for (r in 1 until nn) {
                var cc = 0
                for (c in 0 until nn) if (c != j) {
                    minor[r - 1][cc] = m[r][c]
                    cc++
                }
            }
            val minorDet = detRecursive(minor, depth + 1, "minor(1,${j + 1})")
            val cof = if ((1 + (j + 1)) % 2 == 0) -minorDet else minorDet
            log.add("${"  ".repeat(depth)}C1,${j + 1} = (-1)^{1+${j + 1}}·M1,${j + 1} = ${fmt(cof)}")
            val term = m[0][j] * cof
            log.add("${"  ".repeat(depth)}a1,${j + 1}·C1,${j + 1} = ${fmt(m[0][j])}·${fmt(cof)} = ${fmt(term)}")
            sum += term
        }

        log.add("${"  ".repeat(depth)}det($label) = ${fmt(sum)}")
        return sum
    }

    val det = detRecursive(a, 0, "A")
    return DetResult(det, log.steps)
}

// Determinan dengan metode Sarrus (khusus untuk matriks 3x3)
fun determinantSarrus3(a: Array<DoubleArray>): DetResult {
    require(a.size == 3 && a.all { it.size == 3 }) { "Sarrus hanya untuk 3×3" }
    val log = StepLogger()

    val d1 = a[0][0] * a[1][1] * a[2][2]
    val d2 = a[0][1] * a[1][2] * a[2][0]
    val d3 = a[0][2] * a[1][0] * a[2][1]

    val u1 = a[0][2] * a[1][1] * a[2][0]
    val u2 = a[0][0] * a[1][2] * a[2][1]
    val u3 = a[0][1] * a[1][0] * a[2][2]

    log.add("Σ diagonal turun = ${fmt(d1)} + ${fmt(d2)} + ${fmt(d3)}")
    log.add("Σ diagonal naik  = ${fmt(u1)} + ${fmt(u2)} + ${fmt(u3)}")

    val det = (d1 + d2 + d3) - (u1 + u2 + u3)
    log.add("det(a) = (Σ turun) − (Σ naik) = ${fmt(det)}")

    return DetResult(det, log.steps)
}