package com.inforable.irk_library.core.matrix.steps

import java.util.Locale

class StepLogger {
    private val _steps = mutableListOf<Step>()
    val steps: List<Step> get() = _steps

    // Menambah step baru ke daftar langkah
    fun add(line: String) { _steps += Step(line) }

    // Menambah step baru ke daftar langkah dengan format string dinamis
    fun addf(fmt: String, vararg args: Any?) {
        _steps += Step(String.format(Locale.US, fmt, *args))
    }

    // Menmabah step yang multi-line, seperti kondisi matrix saat ini
    fun snapshot(title: String?, block: String) {
        _steps += Step(buildString {
            if (!title.isNullOrBlank()) appendLine(title)
            append(block)
        }. trimEnd())
    }
}