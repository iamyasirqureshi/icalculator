package com.example.icalculator.model

data class CalculatorState(
    val display: String = "0",
    val expression: String = "",
    val firstOperand: Double? = null,
    val operator: String? = null,
    val resetOnNextInput: Boolean = false,
    val justCalculated: Boolean = false,
    val isError: Boolean = false,
    val cursorPosition: Int = 0
)

enum class ButtonType { NUMBER, NUMBER_WIDE, FUNCTION, OPERATOR, EQUALS }

data class CalcButton(val label: String, val type: ButtonType)

data class HistoryItem(
    val expression: String,
    val result: String
)

// Row 1: AC  +/-  %   ÷
// Row 2: 7   8    9   ×
// Row 3: 4   5    6   −
// Row 4: 1   2    3   +
// Row 5: 0(wide)  .   ⌫   =
val calculatorButtons = listOf(
    listOf(
        CalcButton("AC",  ButtonType.FUNCTION),
        CalcButton("+/-", ButtonType.FUNCTION),
        CalcButton("%",   ButtonType.FUNCTION),
        CalcButton("÷",   ButtonType.OPERATOR)
    ),
    listOf(
        CalcButton("7", ButtonType.NUMBER),
        CalcButton("8", ButtonType.NUMBER),
        CalcButton("9", ButtonType.NUMBER),
        CalcButton("×", ButtonType.OPERATOR)
    ),
    listOf(
        CalcButton("4", ButtonType.NUMBER),
        CalcButton("5", ButtonType.NUMBER),
        CalcButton("6", ButtonType.NUMBER),
        CalcButton("−", ButtonType.OPERATOR)
    ),
    listOf(
        CalcButton("1", ButtonType.NUMBER),
        CalcButton("2", ButtonType.NUMBER),
        CalcButton("3", ButtonType.NUMBER),
        CalcButton("+", ButtonType.OPERATOR)
    ),
    listOf(

            CalcButton("0", ButtonType.NUMBER),
            CalcButton(".", ButtonType.NUMBER),
            CalcButton("⌫", ButtonType.FUNCTION),
            CalcButton("=", ButtonType.EQUALS)

    )
)