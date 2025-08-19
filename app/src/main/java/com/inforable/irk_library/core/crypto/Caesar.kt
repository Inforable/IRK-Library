package com.inforable.irk_library.core.crypto

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger

data class CaesarResult(val output: String, val steps: List<Step>)

// operasi mod dengan handler untuk bil negatif juga
private fun mod(a: Int, m: Int): Int {
    val r = a % m
    return if (r < 0) r + m else r
}

fun caesar26Encrypt(text: String, shift: Int): CaesarResult {
    val log = StepLogger()
    val out = StringBuilder()
    log.add("Enkripsi Caesar (26 huruf): c = (p + k) mod 26, k = $shift")
    for (ch in text) {
        if (ch.isLetter()) {
            val base = if (ch.isUpperCase()) 'A' else 'a'
            val p = ch.code - base.code
            val c = mod(p + shift, 26)
            val outCh = (base.code + c).toChar()
            log.add("$ch -> p=$p -> c=(p+k) mod 26 = $c -> $outCh")
            out.append(outCh)
        } else {
            log.add("$ch (non-huruf) -> tidak digeser")
            out.append(ch)
        }
    }
    return CaesarResult(out.toString(), log.steps)
}

fun caesar26Decrypt(text: String, shift: Int): CaesarResult {
    val log = StepLogger()
    val out = StringBuilder()
    log.add("Dekripsi Caesar (26 huruf): p = (c - k) mod 26, k = $shift")
    for (ch in text) {
        if (ch.isLetter()) {
            val base = if (ch.isUpperCase()) 'A' else 'a'
            val c = ch.code - base.code
            val p = mod(c - shift, 26)
            val outCh = (base.code + p).toChar()
            log.add("$ch -> c=$c -> p=(c−k) mod 26 = $p -> $outCh")
            out.append(outCh)
        } else {
            log.add("$ch (non-huruf) -> tidak digeser")
            out.append(ch)
        }
    }
    return CaesarResult(out.toString(), log.steps)
}

