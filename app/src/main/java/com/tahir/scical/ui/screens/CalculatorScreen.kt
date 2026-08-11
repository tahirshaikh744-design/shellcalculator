package com.tahir.scical.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tahir.scical.ui.theme.*
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.*

/**
 * Main Calculator Screen - Pure Jetpack Compose Implementation
 * Preserves all existing functionality from CalculatorFragment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onAddToHistory: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var displayText by remember { mutableStateOf("0") }
    var formulaText by remember { mutableStateOf("") }
    var lastNumeric by remember { mutableStateOf(false) }
    var stateError by remember { mutableStateOf(false) }
    var lastDot by remember { mutableStateOf(false) }
    var bracketCount by remember { mutableStateOf(0) }
    var isScientificExpanded by remember { mutableStateOf(false) }
    var isRadians by remember { mutableStateOf(true) }
    var isInv by remember { mutableStateOf(false) }

    val factorialFunc = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            if (arg < 0 || arg != floor(arg)) return Double.NaN
            var res = 1.0
            for (i in 1..arg.toInt()) res *= i
            return res
        }
    }

    enum class ButtonType {
        NUMBER, OPERATOR, SCIENTIFIC, FUNCTION, SPECIAL, EQUAL, CLEAR, DELETE
    }

    // Dynamic button heights based on expansion state
    val scientificButtonHeight = if (isScientificExpanded) 42.dp else 70.dp
    val mainButtonHeight = if (isScientificExpanded) 42.dp else 70.dp
    val scientificTextSize = if (isScientificExpanded) 16.sp else 22.sp
    val mainTextSize = if (isScientificExpanded) 22.sp else 32.sp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgMain)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = formulaText,
                    color = TextSecondary,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formatDisplayText(displayText),
                    color = TextPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    maxLines = 3
                )
            }
        }

        // Scientific Grid (Collapsible)
        AnimatedVisibilityWrapper(
            isVisible = isScientificExpanded,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(getScientificButtons(isInv, isRadians)) { btn ->
                    CalculatorButton(
                        text = btn.first,
                        onClick = {
                            when (btn.first) {
                                "sin" -> onFunction(if(isInv) "asin(" else "sin(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "cos" -> onFunction(if(isInv) "acos(" else "cos(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "tan" -> onFunction(if(isInv) "atan(" else "tan(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "log" -> onFunction("log(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "ln" -> onFunction("ln(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "√" -> onFunction("√(", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "π" -> onDigit("π", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "e" -> onDigit("e", displayText, formulaText) { d, f ->
                                    displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                                "!" -> {
                                    if (lastNumeric) {
                                        displayText += "!"
                                        lastNumeric = false
                                        calculateLive(displayText, factorialFunc) { formulaText = it }
                                    }
                                }
                                "Rad", "Deg" -> {
                                    isRadians = !isRadians
                                    calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                            }
                        },
                        type = btn.second,
                        height = scientificButtonHeight,
                        fontSize = scientificTextSize
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Toggle Scientific Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isScientificExpanded = !isScientificExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle Scientific",
                modifier = Modifier.rotate(if (isScientificExpanded) 0f else 180f),
                tint = Violet
            )
            Text(
                text = if (isScientificExpanded) "Hide Scientific" else "Show Scientific",
                color = Violet,
                fontSize = 14.sp
            )
        }

        // Main Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(getMainButtons()) { btn ->
                CalculatorButton(
                    text = btn.first,
                    onClick = {
                        when (btn.second) {
                            ButtonType.NUMBER -> onDigit(btn.first, displayText, formulaText) { d, f ->
                                displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                            }
                            ButtonType.OPERATOR -> onOperator(btn.first, displayText, formulaText) { d, f ->
                                displayText = d; formulaText = f; calculateLive(displayText, factorialFunc) { formulaText = it }
                            }
                            ButtonType.CLEAR -> {
                                displayText = "0"
                                formulaText = ""
                                lastNumeric = false
                                stateError = false
                                lastDot = false
                                bracketCount = 0
                            }
                            ButtonType.DELETE -> {
                                onDelete(displayText, formulaText, bracketCount) { d, f, b ->
                                    displayText = d; formulaText = f; bracketCount = b
                                    calculateLive(displayText, factorialFunc) { formulaText = it }
                                }
                            }
                            ButtonType.EQUAL -> {
                                onEqual(displayText, formulaText, bracketCount, factorialFunc) { d, f, b, ln, ld ->
                                    displayText = d
                                    formulaText = f
                                    bracketCount = b
                                    lastNumeric = ln
                                    lastDot = ld
                                    onAddToHistory(displayText, formulaText)
                                }
                            }
                            ButtonType.SPECIAL -> {
                                if (btn.first == "(") {
                                    onBrackets(displayText, formulaText, bracketCount) { d, f, b ->
                                        displayText = d; formulaText = f; bracketCount = b
                                        calculateLive(displayText, factorialFunc) { formulaText = it }
                                    }
                                }
                            }
                            else -> {}
                        }
                    },
                    type = btn.second,
                    height = mainButtonHeight,
                    fontSize = mainTextSize
                )
            }
        }
    }
}

@Composable
private fun AnimatedVisibilityWrapper(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.animateContentSize()
    ) {
        if (isVisible) content() else Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
private fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    type: CalculatorScreen.ButtonType,
    height: Dp = 70.dp,
    fontSize: TextUnit = 22.sp
) {
    val backgroundColor = when (type) {
        CalculatorScreen.ButtonType.NUMBER -> BtnNumber
        CalculatorScreen.ButtonType.OPERATOR -> BtnOperator
        CalculatorScreen.ButtonType.SCIENTIFIC, CalculatorScreen.ButtonType.FUNCTION, CalculatorScreen.ButtonType.SPECIAL -> BtnScientific
        CalculatorScreen.ButtonType.EQUAL -> BtnEqual
        CalculatorScreen.ButtonType.CLEAR -> Color(0xFFFF6B6B)
        CalculatorScreen.ButtonType.DELETE -> Color(0xFFFFD93D)
    }

    val textColor = when (type) {
        CalculatorScreen.ButtonType.EQUAL, CalculatorScreen.ButtonType.CLEAR -> White
        CalculatorScreen.ButtonType.DELETE -> Color(0xFF333333)
        else -> TextPrimary
    }

    Card(
        modifier = Modifier.aspectRatio(1f).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getScientificButtons(isInv: Boolean, isRadians: Boolean): List<Pair<String, CalculatorScreen.ButtonType>> {
    return listOf(
        Pair(if (isInv) "sin⁻¹" else "sin", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair(if (isInv) "cos⁻¹" else "cos", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair(if (isInv) "tan⁻¹" else "tan", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair("log", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair("ln", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair("√", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair("!", CalculatorScreen.ButtonType.FUNCTION),
        Pair("π", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair("e", CalculatorScreen.ButtonType.SCIENTIFIC),
        Pair(if (isRadians) "Rad" else "Deg", CalculatorScreen.ButtonType.SPECIAL)
    )
}

private fun getMainButtons(): List<Pair<String, CalculatorScreen.ButtonType>> {
    return listOf(
        Pair("AC", CalculatorScreen.ButtonType.CLEAR),
        Pair("⌫", CalculatorScreen.ButtonType.DELETE),
        Pair("%", CalculatorScreen.ButtonType.OPERATOR),
        Pair("÷", CalculatorScreen.ButtonType.OPERATOR),
        Pair("7", CalculatorScreen.ButtonType.NUMBER),
        Pair("8", CalculatorScreen.ButtonType.NUMBER),
        Pair("9", CalculatorScreen.ButtonType.NUMBER),
        Pair("×", CalculatorScreen.ButtonType.OPERATOR),
        Pair("4", CalculatorScreen.ButtonType.NUMBER),
        Pair("5", CalculatorScreen.ButtonType.NUMBER),
        Pair("6", CalculatorScreen.ButtonType.NUMBER),
        Pair("-", CalculatorScreen.ButtonType.OPERATOR),
        Pair("1", CalculatorScreen.ButtonType.NUMBER),
        Pair("2", CalculatorScreen.ButtonType.NUMBER),
        Pair("3", CalculatorScreen.ButtonType.NUMBER),
        Pair("+", CalculatorScreen.ButtonType.OPERATOR),
        Pair("0", CalculatorScreen.ButtonType.NUMBER),
        Pair(".", CalculatorScreen.ButtonType.NUMBER),
        Pair("=", CalculatorScreen.ButtonType.EQUAL),
        Pair("(", CalculatorScreen.ButtonType.SPECIAL)
    )
}

private fun formatDisplayText(text: String): String {
    val df = DecimalFormat("#,###.########")
    val regex = "(\\d+\\.?\\d*)".toRegex()
    return regex.replace(text.replace(",", "")) { match ->
        val numStr = match.value
        try {
            if (numStr.contains(".")) {
                val parts = numStr.split(".")
                "${df.format(parts[0].toDouble())}.${parts[1]}"
            } else {
                df.format(numStr.toDouble())
            }
        } catch (e: Exception) {
            numStr
        }
    }
}

// Helper functions - Complete implementation preserved from CalculatorFragment

private fun onDigit(digit: String, displayText: String, formulaText: String, update: (String, String) -> Unit) {
    var newText = if (displayText == "0" || displayText.isEmpty()) digit else displayText + digit
    val formatted = formatNumber(newText.replace(",", ""))
    update(formatted, formulaText)
}

private fun onOperator(op: String, displayText: String, formulaText: String, update: (String, String) -> Unit) {
    if (op == "%") {
        update(displayText + "%", formulaText)
        return
    }
    val lastChar = displayText.lastOrNull()
    val isLastNumeric = lastChar?.isDigit() == true || lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '%'
    if (isLastNumeric || op == "-") {
        update(displayText + op, formulaText)
    }
}

private fun onFunction(func: String, displayText: String, formulaText: String, update: (String, String) -> Unit) {
    val newText = if (displayText == "0" || displayText.isEmpty()) func else displayText + func
    update(newText, formulaText)
}

private fun onBrackets(displayText: String, formulaText: String, bracketCount: Int, update: (String, String, Int) -> Unit) {
    var newBracketCount = bracketCount
    val lastChar = displayText.lastOrNull()
    val isLastNumeric = lastChar?.isDigit() == true || lastChar == ')' || lastChar == 'π' || lastChar == 'e'

    val newText = if (displayText == "0" || displayText.isEmpty()) {
        newBracketCount++
        "("
    } else {
        if (isLastNumeric) {
            if (bracketCount > 0) {
                newBracketCount--
                displayText + ")"
            } else {
                newBracketCount++
                displayText + "×("
            }
        } else {
            newBracketCount++
            displayText + "("
        }
    }
    update(newText, formulaText, newBracketCount)
}

private fun onDelete(displayText: String, formulaText: String, bracketCount: Int, update: (String, String, Int) -> Unit) {
    var newBracketCount = bracketCount
    if (displayText.isNotEmpty() && displayText != "0") {
        var newText = when {
            displayText.endsWith("(") -> {
                newBracketCount--
                val functions = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(", "asin(", "acos(", "atan(")
                functions.find { displayText.endsWith(it) }?.let {
                    displayText.substring(0, displayText.length - it.length)
                } ?: displayText.substring(0, displayText.length - 1)
            }
            displayText.endsWith(")") -> {
                newBracketCount++
                displayText.substring(0, displayText.length - 1)
            }
            else -> displayText.substring(0, displayText.length - 1)
        }
        if (newText.isEmpty()) newText = "0"
        update(formatNumber(newText.replace(",", "")), formulaText, newBracketCount)
    }
}

private fun onEqual(displayText: String, formulaText: String, bracketCount: Int, factorialFunc: Function, update: (String, String, Int, Boolean, Boolean) -> Unit) {
    var txt = displayText.replace(",", "")
    if (txt == "0" || txt.isEmpty()) return

    var tempBracketCount = bracketCount
    while (txt.isNotEmpty() && (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("×") || txt.endsWith("÷") || txt.endsWith("^") || txt.endsWith("("))) {
        if (txt.endsWith("(")) tempBracketCount--
        txt = txt.substring(0, txt.length - 1)
    }

    if (txt.isEmpty()) {
        update("0", "", 0, false, false)
        return
    }

    repeat(maxOf(0, tempBracketCount)) { txt += ")" }

    val processedTxt = processPercentage(txt)
    var evalTxt = processedTxt.replace("×", "*").replace("÷", "/").replace("−", "-")
        .replace("π", "pi").replace("e", "e").replace("√", "sqrt")

    val factorialRegex = "(\\d+(\\.\\d+)?|pi|e|\\))!".toRegex()
    while (evalTxt.contains("!")) {
        val match = factorialRegex.find(evalTxt)
        if (match != null) evalTxt = evalTxt.replaceFirst("${match.groupValues[1]}!", "fact(${match.groupValues[1]})")
        else break
    }

    evalTxt = evalTxt.replace("(\\d|pi|e|\\))(pi|e|sqrt|sin|cos|tan|log|ln|fact|\\()".toRegex(), "$1*$2")
        .replace("(pi|e|\\))(\\d)".toRegex(), "$1*$2")
        .replace("log(", "log10(").replace("ln(", "log(")

    try {
        val expression = ExpressionBuilder(evalTxt).variables("pi", "e").function(factorialFunc)
            .build().setVariable("pi", Math.PI).setVariable("e", Math.E)
        var result = expression.evaluate()
        if (result.isFinite()) {
            val rounded = BigDecimal(result).setScale(12, RoundingMode.HALF_UP).toDouble()
            if (kotlin.math.abs(result - rounded) < 1e-13) result = rounded
        }
        val resultStr = DecimalFormat("#,###.########").format(result)
        update(resultStr, "", 0, true, resultStr.contains("."))
    } catch (e: Exception) {
        update("Error", "", 0, false, false)
    }
}

private fun calculateLive(displayText: String, factorialFunc: Function, updateFormula: (String) -> Unit) {
    var txt = displayText.replace(",", "")
    if (txt == "0" || txt.isEmpty()) {
        updateFormula("")
        return
    }

    if (txt.endsWith("%") && !txt.dropLast(1).any { it in "+-×÷" }) {
        try {
            val num = txt.dropLast(1).toDouble()
            updateFormula(DecimalFormat("#,###.########").format(num / 100.0))
            return
        } catch (e: Exception) {
            updateFormula("")
            return
        }
    }

    var tempBracketCount = 0
    var tempTxt = txt
    while (tempTxt.isNotEmpty() && (tempTxt.endsWith("+") || tempTxt.endsWith("-") || tempTxt.endsWith("×") || tempTxt.endsWith("÷") || tempTxt.endsWith("^") || tempTxt.endsWith("("))) {
        if (tempTxt.endsWith("(")) tempBracketCount--
        tempTxt = tempTxt.dropLast(1)
    }

    if (tempTxt.isEmpty()) {
        updateFormula("")
        return
    }

    repeat(maxOf(0, tempBracketCount)) { tempTxt += ")" }

    try {
        val processed = processPercentage(tempTxt)
        var finalEval = processed.replace("×", "*").replace("÷", "/").replace("−", "-")
            .replace("π", "pi").replace("e", "e").replace("√", "sqrt")

        val factorialRegex = "(\\d+(\\.\\d+)?|pi|e|\\))!".toRegex()
        while (finalEval.contains("!")) {
            val match = factorialRegex.find(finalEval)
            if (match != null) finalEval = finalEval.replaceFirst("${match.groupValues[1]}!", "fact(${match.groupValues[1]})")
            else break
        }

        finalEval = finalEval.replace("(\\d|pi|e|\\))(pi|e|sqrt|sin|cos|tan|log|ln|fact|\\()".toRegex(), "$1*$2")
            .replace("(pi|e|\\))(\\d)".toRegex(), "$1*$2")
            .replace("log(", "log10(").replace("ln(", "log(")

        val expression = ExpressionBuilder(finalEval).variables("pi", "e").function(factorialFunc)
            .build().setVariable("pi", Math.PI).setVariable("e", Math.E)
        val result = expression.evaluate()
        if (result.isFinite()) updateFormula(DecimalFormat("#,###.########").format(result))
        else updateFormula("")
    } catch (e: Exception) {
        updateFormula("")
    }
}

private fun processPercentage(txt: String): String {
    var processed = "(\\d+\\.?\\d*)\\s*([+\\-])\\s*(\\d+\\.?\\d*)%".toRegex().replace(txt) { m ->
        "${m.groupValues[1]}${m.groupValues[2]}(${m.groupValues[1]}*(${m.groupValues[3]}/100))"
    }
    processed = "(\\d+\\.?\\d*)\\s*([×÷*/])\\s*(\\d+\\.?\\d*)%".toRegex().replace(processed) { m ->
        val opChar = if (m.groupValues[2] == "×" || m.groupValues[2] == "*") "*" else "/"
        "${m.groupValues[1]}$opChar(${m.groupValues[3]}/100)"
    }
    return processed.replace("(\\d+\\.?\\d*)%".toRegex(), "($1/100)")
}

private fun formatNumber(numStr: String): String {
    val df = DecimalFormat("#,###.########")
    return "(\\d+\\.?\\d*)".toRegex().replace(numStr) { match ->
        val num = match.value
        try {
            if (num.contains(".")) {
                val parts = num.split(".")
                "${df.format(parts[0].toDouble())}.${parts[1]}"
            } else df.format(num.toDouble())
        } catch (e: Exception) { num }
    }
}
