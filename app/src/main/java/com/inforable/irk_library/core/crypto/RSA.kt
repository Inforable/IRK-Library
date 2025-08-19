package com.inforable.irk_library.core.crypto

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.math.BigInteger

data class RSAKey(val p: BigInteger, val q: BigInteger, val n: BigInteger, val phi: BigInteger, val e: BigInteger, val d: BigInteger, val steps: List<Step>)
data class RSAEnc(val cipherBlocks: List<BigInteger>, val steps: List<Step>)
data class RSADec(val plainText: String, val steps: List<Step>)

private fun fmt(x: BigInteger) = x.toString()

private fun modInverseWithSteps(e: BigInteger, m: BigInteger): Pair<BigInteger, List<Step>> {
    val log = StepLogger()
    var a = m; var b = e
    var x0 = BigInteger.ONE; var y0 = BigInteger.ZERO
    var x1 = BigInteger.ZERO; var y1 = BigInteger.ONE
    log.add("Euclid diperluas untuk d ≡ e^{-1} (mod φ)")
    while (b != BigInteger.ZERO) {
        val q = a / b
        val r = a % b
        val nx = x0 - q * x1
        val ny = y0 - q * y1
        log.add("${fmt(a)} = ${fmt(b)}·${fmt(q)} + ${fmt(r)}")
        a = b; b = r
        x0 = x1; y0 = y1
        x1 = nx; y1 = ny
    }
    require(a == BigInteger.ONE) { "gcd(e, φ) ≠ 1 -> inverse tidak ada" }
    var d = y0.mod(m)
    if (d < BigInteger.ZERO) d += m
    log.add("d = ${fmt(d)}")
    return d to log.steps
}

private fun powModSteps(base: BigInteger, exp: BigInteger, mod: BigInteger): Pair<BigInteger, List<Step>> {
    val log = StepLogger()
    var b = base.mod(mod)
    var e = exp
    var r = BigInteger.ONE
    log.add("Hitung ${fmt(base)}^${fmt(exp)} mod ${fmt(mod)} (square-and-multiply)")
    while (e > BigInteger.ZERO) {
        if (e.and(BigInteger.ONE) == BigInteger.ONE) {
            r = r.multiply(b).mod(mod)
            log.add("multiply -> r = r·b mod m = ${fmt(r)}")
        }
        b = b.multiply(b).mod(mod)
        e = e.shiftRight(1)
        log.add("square -> b = b² mod m = ${fmt(b)} ; e = e/2")
    }
    return r to log.steps
}

private fun textToBlocks(text: String, blockSize: Int, n: BigInteger): Pair<List<BigInteger>, List<Step>> {
    val log = StepLogger()
    val upper = text.uppercase()
    val codes = buildString {
        for (ch in upper) {
            val code = when (ch) {
                ' ' -> "00"
                in 'A'..'Z' -> (ch.code - 'A'.code + 1).toString().padStart(2, '0')
                else -> "00" // selain A..Z/spasi dipetakan ke 00 agar simpel
            }
            append(code)
        }
    }
    log.add("Mapping A..Z -> 01..26, spasi -> 00: $codes")
    val chunks = codes.chunked(blockSize).map { it.padEnd(blockSize, '0') }
    log.add("Kelompok $blockSize digit: ${chunks.joinToString(" ")}")
    val blocks = chunks.map { BigInteger(it) }
    blocks.forEach { require(it < n) { "Blok $it >= n; besarkan n atau kecilkan blockSize" } }
    return blocks to log.steps
}

private fun blocksToText(blocks: List<BigInteger>, blockSize: Int): Pair<String, List<Step>> {
    val log = StepLogger()
    val joined = blocks.joinToString("") { it.toString().padStart(blockSize, '0') }
    log.add("Gabung blok (pad $blockSize digit): $joined")
    val chars = joined.chunked(2).map { it.toInt() }.map { code ->
        when (code) {
            0 -> ' '
            in 1..26 -> ('A'.code + code - 1).toChar()
            else -> ' '
        }
    }
    val text = chars.joinToString("")
    log.add("Konversi 2-digit -> teks: \"$text\"")
    return text to log.steps
}

fun rsaGenerateKeySimple(p: BigInteger, q: BigInteger, e: BigInteger): RSAKey {
    val log = StepLogger()
    val n = p * q
    val phi = (p - BigInteger.ONE) * (q - BigInteger.ONE)
    log.add("p=$p, q=$q -> n=p·q=$n, φ(n)=(p−1)(q−1)=$phi")
    require(e.gcd(phi) == BigInteger.ONE) { "gcd(e, φ(n)) harus 1" }
    val (d, steps) = modInverseWithSteps(e, phi)
    log.steps.addAll(steps)
    log.add("Public (e,n)=($e,$n); Private (d,n)=($d,$n)")
    return RSAKey(p, q, n, phi, e, d, log.steps)
}

fun rsaEncryptSimple(plain: String, e: BigInteger, n: BigInteger, blockSize: Int = 3): RSAEnc {
    val (blocks, s1) = textToBlocks(plain, blockSize, n)
    val log = StepLogger(); log.steps.addAll(s1)
    val out = mutableListOf<BigInteger>()
    blocks.forEachIndexed { i, m ->
        val (c, s) = powModSteps(m, e, n)
        log.steps.addAll(s)
        log.add("p${i+1}=$m -> c${i+1}=p^e mod n = $c")
        out += c
    }
    return RSAEnc(out, log.steps)
}

fun rsaDecryptSimple(cipherBlocks: List<BigInteger>, d: BigInteger, n: BigInteger, blockSize: Int = 3): RSADec {
    val log = StepLogger()
    val plains = mutableListOf<BigInteger>()
    cipherBlocks.forEachIndexed { i, c ->
        val (m, s) = powModSteps(c, d, n)
        log.steps.addAll(s)
        log.add("c${i+1}=$c -> p${i+1}=c^d mod n = $m")
        plains += m
    }
    val (text, s2) = blocksToText(plains, blockSize)
    log.steps.addAll(s2)
    return RSADec(text, log.steps)
}


private fun List<Step>.addAll(steps: List<Step>) {
    this.addAll(steps)
}