package com.inforable.irk_library.ui.crypto

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.inforable.irk_library.core.crypto.*
import com.inforable.irk_library.core.matrix.steps.Step
import java.math.BigInteger

data class CaesarState(
    val shift: String = "3",
    val input: String = "OKE GAS OKE GAS",
    val output: String = "",
    val steps: List<Step> = emptyList(),
    val showSteps: Boolean = false,
    val error: String? = null
)

data class RSAState(
    val p: String = "47",
    val q: String = "71",
    val e: String = "79",
    val n: String = "",
    val phi: String = "",
    val d: String = "",

    val message: String = "NOMOR 2 TORANG GAS",
    val blockSize: String = "3",
    val cipherBlocks: String = "",
    val plainOut: String = "",

    val keySteps: List<Step> = emptyList(),
    val encSteps: List<Step> = emptyList(),
    val decSteps: List<Step> = emptyList(),
    val showKeySteps: Boolean = false,
    val showEncSteps: Boolean = false,
    val showDecSteps: Boolean = false,
    val error: String? = null
)

data class CryptoLiteUiState(
    val caesar: CaesarState = CaesarState(),
    val rsa: RSAState = RSAState()
)

class CryptoViewModel : ViewModel() {
    var state by mutableStateOf(CryptoLiteUiState()); private set

    // Caesar
    fun setCaesarShift(s:String){ state = state.copy(caesar = state.caesar.copy(shift = s)) }
    fun setCaesarInput(s:String){ state = state.copy(caesar = state.caesar.copy(input = s)) }
    fun toggleCaesarSteps(){ state = state.copy(caesar = state.caesar.copy(showSteps = !state.caesar.showSteps)) }

    fun caesarEncrypt(){ runCaesar(isEncrypt = true) }
    fun caesarDecrypt(){ runCaesar(isEncrypt = false) }

    private fun runCaesar(isEncrypt:Boolean){
        try {
            val k = state.caesar.shift.toInt()
            val res = if (isEncrypt) caesar26Encrypt(state.caesar.input, k)
            else caesar26Decrypt(state.caesar.input, k)
            state = state.copy(caesar = state.caesar.copy(output = res.output, steps = res.steps, error = null, showSteps = false))
        } catch (e: Exception){
            state = state.copy(caesar = state.caesar.copy(error = e.message, output = "", steps = emptyList(), showSteps = false))
        }
    }

    // RSA
    fun setP(s:String){ state = state.copy(rsa = state.rsa.copy(p = s)) }
    fun setQ(s:String){ state = state.copy(rsa = state.rsa.copy(q = s)) }
    fun setE(s:String){ state = state.copy(rsa = state.rsa.copy(e = s)) }
    fun setMessage(s:String){ state = state.copy(rsa = state.rsa.copy(message = s)) }
    fun setBlockSize(s:String){ state = state.copy(rsa = state.rsa.copy(blockSize = s)) }
    fun setDecInput(s:String){ state = state.copy(rsa = state.rsa.copy(cipherBlocks = s)) } // reuse field for manual input
    fun toggleKeySteps(){ state = state.copy(rsa = state.rsa.copy(showKeySteps = !state.rsa.showKeySteps)) }
    fun toggleEncSteps(){ state = state.copy(rsa = state.rsa.copy(showEncSteps = !state.rsa.showEncSteps)) }
    fun toggleDecSteps(){ state = state.copy(rsa = state.rsa.copy(showDecSteps = !state.rsa.showDecSteps)) }

    fun generateKeys(){
        try {
            val p = BigInteger(state.rsa.p)
            val q = BigInteger(state.rsa.q)
            val e = BigInteger(state.rsa.e)
            val res = rsaGenerateKeySimple(p,q,e)
            state = state.copy(rsa = state.rsa.copy(
                n = res.n.toString(),
                phi = res.phi.toString(),
                d = res.d.toString(),
                keySteps = res.steps,
                error = null,
                showKeySteps = false
            ))
        } catch (ex: Exception){
            state = state.copy(rsa = state.rsa.copy(error = ex.message, keySteps = emptyList(), showKeySteps = false))
        }
    }

    fun encrypt(){
        try {
            val nStr = state.rsa.n; require(nStr.isNotBlank()) { "Generate key dulu" }
            val e = BigInteger(state.rsa.e)
            val n = BigInteger(nStr)
            val b = state.rsa.blockSize.toInt()
            val enc = rsaEncryptSimple(state.rsa.message, e, n, b)
            val blocksStr = enc.cipherBlocks.joinToString(" ")
            state = state.copy(rsa = state.rsa.copy(cipherBlocks = blocksStr, encSteps = enc.steps, error = null, showEncSteps = false))
        } catch (ex: Exception){
            state = state.copy(rsa = state.rsa.copy(error = ex.message, encSteps = emptyList(), showEncSteps = false))
        }
    }

    fun decrypt(){
        try {
            val dStr = state.rsa.d; val nStr = state.rsa.n
            require(dStr.isNotBlank() && nStr.isNotBlank()) { "Private key belum lengkap (d/n kosong)" }
            val d = BigInteger(dStr); val n = BigInteger(nStr)
            val b = state.rsa.blockSize.toInt()
            val blocks = state.rsa.cipherBlocks.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.map { BigInteger(it) }
            val dec = rsaDecryptSimple(blocks, d, n, b)
            state = state.copy(rsa = state.rsa.copy(plainOut = dec.plainText, decSteps = dec.steps, error = null, showDecSteps = false))
        } catch (ex: Exception){
            state = state.copy(rsa = state.rsa.copy(error = ex.message, decSteps = emptyList(), showDecSteps = false))
        }
    }
}
