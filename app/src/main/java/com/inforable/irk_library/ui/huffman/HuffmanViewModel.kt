package com.inforable.irk_library.ui.huffman

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.inforable.irk_library.core.huffman.*
import com.inforable.irk_library.core.matrix.steps.Step

data class HuffmanUiState(
    val input: String = "ABACCDA",
    val built: Boolean = false,
    val freqTable: String = "",
    val asciiTree: String = "",
    val progress: Int = 0,
    val mergesCount: Int = 0,

    val codes: Map<Char,String> = emptyMap(),
    val selectedChar: Char? = null,

    val stepsBuild: List<Step> = emptyList(),
    val showBuildSteps: Boolean = false,

    val bitOut: String = "",
    val stepsEnc: List<Step> = emptyList(),
    val showEncSteps: Boolean = false,

    val decBits: String = "",
    val plainOut: String = "",
    val stepsDec: List<Step> = emptyList(),
    val showDecSteps: Boolean = false,

    val error: String? = null,

    // internal
    val root: HuffmanNode? = null,
    val merges: List<MergeStep> = emptyList()
)

class HuffmanViewModel : ViewModel() {
    var state by mutableStateOf(HuffmanUiState()); private set

    fun setInput(s: String) { state = state.copy(input = s) }
    fun toggleBuildSteps(){ state = state.copy(showBuildSteps = !state.showBuildSteps) }
    fun toggleEncSteps(){ state = state.copy(showEncSteps = !state.showEncSteps) }
    fun toggleDecSteps(){ state = state.copy(showDecSteps = !state.showDecSteps) }
    fun setProgress(v: Int){
        val p = v.coerceIn(0, state.mergesCount)
        state = state.copy(progress = p, asciiTree = renderAsciiTree(state.root, state.merges, p))
    }
    fun selectChar(ch: Char){ state = state.copy(selectedChar = ch) }

    fun build() {
        try {
            val r = buildWithSteps(state.input)
            val freq = state.input.filter { !it.isWhitespace() }.groupingBy { it }.eachCount()
                .toSortedMap().entries.joinToString("  ") { "${it.key}:${it.value}" }
            val treeText = renderAsciiTree(r.root, r.merges, r.merges.size)
            state = state.copy(
                built = true,
                root = r.root,
                codes = r.codes,
                stepsBuild = r.steps,
                merges = r.merges,
                mergesCount = r.merges.size,
                progress = r.merges.size,
                asciiTree = treeText,
                freqTable = freq,
                error = null,
                bitOut = "", stepsEnc = emptyList(), showEncSteps = false,
                decBits = "", plainOut = "", stepsDec = emptyList(), showDecSteps = false,
                selectedChar = null
            )
        } catch (e: Exception){
            state = state.copy(error = e.message)
        }
    }

    fun encode() {
        val root = state.root ?: return
        try {
            val enc = encodeWithSteps(state.input, state.codes)
            state = state.copy(bitOut = enc.bits, stepsEnc = enc.steps, decBits = enc.bits, showEncSteps = false, error = null)
        } catch (e: Exception){ state = state.copy(error = e.message) }
    }

    fun setDecBits(s: String){ state = state.copy(decBits = s.filter { it=='0' || it=='1' }) }

    fun decode() {
        val root = state.root ?: return
        try {
            val dec = decodeWithSteps(state.decBits, root)
            state = state.copy(plainOut = dec.text, stepsDec = dec.steps, showDecSteps = false, error = null)
        } catch (e: Exception){ state = state.copy(error = e.message) }
    }
}

private fun renderAsciiTree(root: HuffmanNode?, merges: List<MergeStep>, progress: Int): String {
    if (root == null) return ""
    // node yang sudah “aktif” setelah 'progress' gabungan:
    val activeParents = merges.take(progress).map { it.parent }.toSet()
    val activeChildren = merges.take(progress).flatMap { listOf(it.left, it.right) }.toSet()
    fun visible(n: HuffmanNode): Boolean {
        // daun selalu kelihatan; internal kelihatan jika sudah jadi “parent” aktif
        return (n.left == null && n.right == null) || n in activeParents || n in activeChildren || progress == merges.size
    }

    val sb = StringBuilder()
    fun go(n: HuffmanNode, prefix: String, edgeLabel: String?, isLast: Boolean) {
        if (!visible(n)) return
        val branch = if (edgeLabel == null) "" else edgeLabel
        val connector = when {
            edgeLabel == null -> ""               // root
            isLast -> "└─$branch─"
            else -> "├─$branch─"
        }
        sb.append(prefix).append(connector).append("${n.label} (${n.freq})").append('\n')

        val childPrefix = when {
            edgeLabel == null -> ""               // root
            isLast -> prefix + "   "
            else -> prefix + "│  "
        }
        val children = listOfNotNull(n.left?.let { "0" to it }, n.right?.let { "1" to it })
        children.forEachIndexed { idx, (lab, child) ->
            go(child, childPrefix, lab, idx == children.lastIndex)
        }
    }
    go(root, prefix = "", edgeLabel = null, isLast = true)
    return sb.toString()
}
