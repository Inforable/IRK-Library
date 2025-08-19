package com.inforable.irk_library.core.huffman

import com.inforable.irk_library.core.matrix.steps.Step
import com.inforable.irk_library.core.matrix.steps.StepLogger
import java.util.PriorityQueue

data class HuffmanNode(
    val label: String, // daun: 1 huruf
    val freq: Int,
    val left: HuffmanNode? = null,
    val right: HuffmanNode? = null,
)

data class MergeStep(val left: HuffmanNode, val right: HuffmanNode, val parent: HuffmanNode)

data class BuildResult(
    val root: HuffmanNode,
    val codes: Map<Char,String>,
    val steps: List<Step>,
    val merges: List<MergeStep>
)

data class EncodeResult(val bits: String, val steps: List<Step>)
data class DecodeResult(val text: String, val steps: List<Step>)

private fun freqs(s: String): Map<Char,Int> =
    s.filter { !it.isWhitespace() }.groupingBy { it }.eachCount()

private fun buildCodes(n: HuffmanNode, pref: String, out: MutableMap<Char,String>) {
    if (n.left == null && n.right == null) {
        val ch = n.label.single()
        out[ch] = if (pref.isEmpty()) "0" else pref // handle 1-simbol
        return
    }
    n.left?.let { buildCodes(it, pref + "0", out) } // kiri = 0
    n.right?.let { buildCodes(it, pref + "1", out) } // kanan = 1
}

fun buildWithSteps(input: String): BuildResult {
    require(input.any { !it.isWhitespace() }) { "Teks kosong" }
    val log = StepLogger()
    val f = freqs(input)

    log.add("Tabel frekuensi: " + f.toSortedMap().entries.joinToString { "${it.key}:${it.value}" })

    val pq = PriorityQueue<HuffmanNode> { a, b ->
        val byF = a.freq.compareTo(b.freq)
        if (byF != 0) byF else a.label.compareTo(b.label)
    }
    f.toSortedMap().forEach { (ch, cnt) -> pq += HuffmanNode(ch.toString(), cnt) }

    val merges = mutableListOf<MergeStep>()
    var step = 1
    while (pq.size > 1) {
        val a = pq.remove()
        val b = pq.remove()
        val (left, right) = if (a.label <= b.label) a to b else b to a
        val p = HuffmanNode(left.label + right.label, left.freq + right.freq, left, right)
        merges += MergeStep(left, right, p)
        log.add("${step}. Gabungkan ${left.label}(${left.freq}) + ${right.label}(${right.freq}) -> ${p.label}(${p.freq})")
        pq += p
        step++
    }
    val root = pq.remove()

    val codes = linkedMapOf<Char,String>()
    buildCodes(root, "", codes)
    codes.forEach { (ch, code) -> log.add("Kode: $ch = $code") }

    return BuildResult(root, codes, log.steps, merges)
}

fun encodeWithSteps(text: String, codes: Map<Char,String>): EncodeResult {
    val log = StepLogger()
    val bits = buildString {
        text.filter { !it.isWhitespace() }.forEach { ch ->
            val code = codes[ch] ?: error("Tidak ada kode untuk '$ch'")
            log.add("Encode '$ch' → $code")
            append(code)
        }
    }
    log.add("Rangkaian bit: $bits (len=${bits.length})")
    return EncodeResult(bits, log.steps)
}

fun decodeWithSteps(bits: String, root: HuffmanNode): DecodeResult {
    val log = StepLogger()
    val out = StringBuilder()
    var cur: HuffmanNode? = root
    bits.forEach { c ->
        cur = if (c == '0') cur?.left else cur?.right
        if (cur?.left == null && cur?.right == null) {
            out.append(cur!!.label)
            log.add("Decode -> '${cur!!.label}'")
            cur = root
        }
    }
    return DecodeResult(out.toString(), log.steps)
}
