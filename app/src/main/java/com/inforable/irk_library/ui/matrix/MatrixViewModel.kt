package com.inforable.irk_library.ui.matrix

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.inforable.irk_library.core.matrix.determinant.*
import com.inforable.irk_library.core.matrix.inverse.*
import com.inforable.irk_library.core.matrix.spl.*
import com.inforable.irk_library.core.matrix.cramer.*
import com.inforable.irk_library.core.matrix.steps.Step

enum class Operation { SPL, DETERMINANT, INVERSE, CRAMER }

data class MatrixUiState(
    val n: Int = 3,
    val A: List<MutableList<String>> = List(3){ MutableList(3){ "0" } },
    val b: MutableList<String> = MutableList(3){ "0" },

    val op: Operation = Operation.SPL,
    val detMethod: DetMethod = DetMethod.OBE,
    val invMethod: InvMethod = InvMethod.GAUSS_JORDAN,
    val pivotGreedy: Boolean = true,

    val resultText: String = "",
    val steps: List<Step> = emptyList(),
    val showSteps: Boolean = false,
    val error: String? = null
)

class MatrixViewModel : ViewModel() {
    var state by mutableStateOf(MatrixUiState())
        private set

    fun setSize(n: Int) {
        val newA = List(n){ r-> MutableList(n){ c-> if(r<state.A.size && c<state.A.size) state.A[r][c] else "0" } }
        val newb = MutableList(n){ i-> if(i<state.b.size) state.b[i] else "0" }
        state = state.copy(n=n, A=newA, b=newb, resultText="", steps= emptyList(), error=null, showSteps=false)
    }
    fun setA(r:Int,c:Int,v:String){ val newA = state.A.map{ it.toMutableList() }.toMutableList(); newA[r][c]=v; state=state.copy(A=newA) }
    fun setB(i:Int,v:String){ val newb = state.b.toMutableList(); newb[i]=v; state=state.copy(b=newb) }

    fun setOp(op: Operation){ state = state.copy(op=op, resultText="", steps= emptyList(), error=null, showSteps=false) }
    fun setDetMethod(m: DetMethod){ state = state.copy(detMethod=m, resultText="", steps= emptyList(), error=null, showSteps=false) }
    fun setInvMethod(m: InvMethod){ state = state.copy(invMethod=m, resultText="", steps= emptyList(), error=null, showSteps=false) }
    fun setPivotGreedy(v:Boolean){ state = state.copy(pivotGreedy=v) }
    fun setShowSteps(v:Boolean){ state = state.copy(showSteps=v) }

    private fun toMatrix(): Array<DoubleArray> = Array(state.n){ r-> DoubleArray(state.n){ c-> state.A[r][c].toDoubleOrNull() ?: 0.0 } }
    private fun toVector(): DoubleArray = DoubleArray(state.n){ i-> state.b[i].toDoubleOrNull() ?: 0.0 }

    fun solve() {
        val A = toMatrix(); val b = toVector()
        try {
            val (text, steps) = when(state.op){
                Operation.SPL -> {
                    val rep = solveSPLGaussJordan(A,b,state.pivotGreedy)
                    rep.resultText to rep.steps
                }
                Operation.DETERMINANT -> {
                    val res = when(state.detMethod){
                        DetMethod.OBE -> determinantOBE(A)
                        DetMethod.COFACTOR -> determinantCofactor(A)
                        DetMethod.SARRUS3 -> { require(A.size==3){"Sarrus hanya 3×3"}; determinantSarrus3(A) }
                    }
                    "det(A) = ${"%.6f".format(res.determinant)}" to res.steps
                }
                Operation.INVERSE -> {
                    val inv = when(state.invMethod){
                        InvMethod.GAUSS_JORDAN -> inverseGaussJordan(A)
                        InvMethod.ADJOINT -> inverseAdjoint(A)
                    }
                    buildString {
                        append("inv(A) =\n")
                        inv.inverse.forEach { row -> append(row.joinToString(prefix="[ ", postfix=" ]") { "%.6f".format(it) }).append('\n') }
                    } to inv.steps
                }
                Operation.CRAMER -> {
                    val rep = solveCramer(A,b,state.detMethod)
                    rep.resultText to rep.steps
                }
            }
            state = state.copy(resultText=text, steps=steps, error=null, showSteps=false)
        } catch (e: Exception){
            state = state.copy(error=e.message, resultText="", steps= emptyList(), showSteps=false)
        }
    }
}
