package com.inforable.irk_library.core.matrix.cramer

import com.inforable.irk_library.core.matrix.determinant.*

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.Locale

data class CramerResult(val resultText: String, val steps: List<Step>)

private const val EPS = 1e-9
private fun approxZero(x: Double) = kotlin.math.abs(x) < EPS
private fun fmt(x: Double) = String.format(Locale.US, "%.6f", x)

fun solveCramer(a: Array<DoubleArray>, b: DoubleArray, detMethod: DetMethod): CramerResult {
    val n = a.size
    require(n > 0 && a.all { it.size == n }) { "A harus persegi" }
    require(b.size == n) { "b harus sepanjang n" }
    val log = StepLogger()

    fun detWithSteps(m: Array<DoubleArray>): DetResult = when (detMethod) {
        DetMethod.OBE -> determinantOBE(m)
        DetMethod.COFACTOR -> determinantCofactor(m)
        DetMethod.SARRUS3 -> {
            require(m.size == 3 && m.all { it.size == 3 }) { "Sarrus hanya utk 3×3" }
            determinantSarrus3(m)
        }
    }

    log.add("== Hitung det(A) ==")
    val detA = detWithSteps(a)
    log.steps.addAll(detA.steps)

    if (approxZero(detA.determinant)) {
        val msg = buildString {
            appendLine("det(A) = ${fmt(detA.determinant)} → Kaidah Cramer tidak berlaku.")
            appendLine("Tidak ada solusi tunggal (bisa tak berhingga atau tidak ada). Gunakan eliminasi Gauss/Jordan untuk klasifikasi.")
        }
        return CramerResult(msg.trimEnd(), log.steps)
    }

    val x = DoubleArray(n)
    for (i in 0 until n) {
        val ai = Array(n) { r -> a[r].clone() }
        for (r in 0 until n) ai[r][i] = b[r]
        log.add("== Bentuk A_${i+1}: ganti kolom ${i+1} pada A dengan b ==")
        val detAi = detWithSteps(ai)
        log.steps.addAll(detAi.steps)
        x[i] = detAi.determinant / detA.determinant
        log.add("x${i+1} = det(A_${i+1}) / det(A) = ${fmt(detAi.determinant)} / ${fmt(detA.determinant)} = ${fmt(x[i])}")
    }

    val report = buildString {
        appendLine("Solusi (Kaidah Cramer):")
        for (i in 0 until n) appendLine("x${i+1} = ${fmt(x[i])}")
    }

    return CramerResult(report.trimEnd(), log.steps)
}

private fun List<Step>.addAll(steps: List<Step>) {
    this.addAll(steps)
}
