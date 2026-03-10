package com.example.icalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.icalculator.model.CalculatorState
import com.example.icalculator.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    fun onButton(label: String) {
        when (label) {
            "AC", "C" -> _state.value = CalculatorState()
            "+/-"     -> toggleSign()
            "%"       -> percent()
            "⌫"       -> backspace()
            "÷", "×", "−", "+" -> inputOperator(label)
            "="       -> equals()
            "."       -> inputDot()
            else      -> inputDigit(label)
        }
    }

    // Called when user taps on a character in the display
    fun setCursor(position: Int) {
        val s = _state.value
        if (s.isError || s.resetOnNextInput) return
        val clamped = position.coerceIn(0, s.display.length)
        _state.value = s.copy(cursorPosition = clamped)
    }

    private fun backspace() {
        val s = _state.value
        if (s.isError || s.resetOnNextInput) {
            _state.value = CalculatorState()
            return
        }
        if (s.display == "0" || s.display.length == 1) {
            _state.value = s.copy(display = "0", cursorPosition = 0)
            return
        }
        val pos = s.cursorPosition.coerceIn(0, s.display.length)
        if (pos == 0) return
        val newDisplay = s.display.removeRange(pos - 1, pos)
        val newPos = (pos - 1).coerceAtLeast(0)
        _state.value = s.copy(
            display = newDisplay,
            cursorPosition = newPos,
            justCalculated = false
        )
    }

    private fun inputDigit(d: String) {
        val s = _state.value
        if (s.isError) {
            _state.value = CalculatorState(display = d, cursorPosition = 1)
            return
        }
        if (s.resetOnNextInput || s.display == "0") {
            _state.value = s.copy(
                display = d,
                cursorPosition = 1,
                resetOnNextInput = false,
                justCalculated = false
            )
            return
        }
        if (s.display.length >= 9) return
        val pos = s.cursorPosition.coerceIn(0, s.display.length)
        val newDisplay = s.display.substring(0, pos) + d + s.display.substring(pos)
        _state.value = s.copy(
            display = newDisplay,
            cursorPosition = pos + 1,
            resetOnNextInput = false,
            justCalculated = false
        )
    }

    private fun inputDot() {
        val s = _state.value
        if (s.isError) return
        val base = if (s.resetOnNextInput) "0" else s.display
        if (base.contains(".")) return
        val pos = if (s.resetOnNextInput) 1 else s.cursorPosition.coerceIn(0, base.length)
        val newDisplay = if (s.resetOnNextInput) "0." else
            base.substring(0, pos) + "." + base.substring(pos)
        _state.value = s.copy(
            display = newDisplay,
            cursorPosition = pos + 1,
            resetOnNextInput = false,
            justCalculated = false
        )
    }

    private fun toggleSign() {
        val s = _state.value
        val v = s.display.toDoubleOrNull() ?: return
        val result = fmt(-v)
        _state.value = s.copy(display = result, cursorPosition = result.length)
    }

    private fun percent() {
        val s = _state.value
        val v = s.display.toDoubleOrNull() ?: return
        val result = if (s.firstOperand != null) s.firstOperand * (v / 100.0) else v / 100.0
        val formatted = fmt(result)
        _state.value = s.copy(
            display = formatted,
            cursorPosition = formatted.length,
            resetOnNextInput = true
        )
    }

    private fun inputOperator(op: String) {
        val s = _state.value
        val current = s.display.toDoubleOrNull() ?: return
        if (s.firstOperand != null && !s.resetOnNextInput && !s.justCalculated) {
            val result = calc(s.firstOperand, current, s.operator!!) ?: return setError()
            val formatted = fmt(result)
            _state.value = s.copy(
                display = formatted,
                expression = "${fmt(result)} $op",
                firstOperand = result,
                operator = op,
                cursorPosition = formatted.length,
                resetOnNextInput = true,
                justCalculated = false
            )
        } else {
            _state.value = s.copy(
                expression = "${fmt(current)} $op",
                firstOperand = current,
                operator = op,
                cursorPosition = s.display.length,
                resetOnNextInput = true,
                justCalculated = false
            )
        }
    }

    private fun equals() {
        val s = _state.value
        val a = s.firstOperand ?: return
        val op = s.operator ?: return
        val b = s.display.toDoubleOrNull() ?: return
        val result = calc(a, b, op) ?: return setError()
        val expr = "${fmt(a)} $op ${fmt(b)}"
        val res = fmt(result)
        _history.value = listOf(HistoryItem(expr, res)) + _history.value
        _state.value = s.copy(
            display = res,
            expression = "$expr =",
            firstOperand = null,
            operator = null,
            cursorPosition = res.length,
            resetOnNextInput = true,
            justCalculated = true
        )
    }

    fun clearHistory() { _history.value = emptyList() }

    private fun setError() {
        _state.value = CalculatorState(display = "Error", isError = true)
    }

    private fun calc(a: Double, b: Double, op: String) = when (op) {
        "+"  -> a + b
        "−"  -> a - b
        "×"  -> a * b
        "÷"  -> if (b == 0.0) null else a / b
        else -> null
    }

    fun fmt(v: Double): String {
        if (v.isInfinite() || v.isNaN()) return "Error"
        return if (v == v.toLong().toDouble()) {
            val l = v.toLong()
            if (l.toString().length > 9) String.format("%.3e", v) else l.toString()
        } else {
            val s = v.toBigDecimal().stripTrailingZeros().toPlainString()
            if (s.length > 9) String.format("%.5g", v) else s
        }
    }

    fun acLabel() = if (_state.value.display == "0"
        && _state.value.firstOperand == null) "AC" else "C"
}