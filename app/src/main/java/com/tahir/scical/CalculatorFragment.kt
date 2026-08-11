package com.tahir.scical

import android.os.*
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.*

class CalculatorFragment : Fragment() {
    private lateinit var tvDisplay: TextView
    private lateinit var tvFormula: TextView
    private lateinit var scientificGrid: GridLayout
    private lateinit var imgToggle: ImageView
    private lateinit var mainGrid: GridLayout
    private lateinit var layoutCalculator: ViewGroup
    
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private var bracketCount = 0
    private var isScientificExpanded = false
    private var isRadians = true
    private var isInv = false

    private val factorialFunc = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            if (arg < 0 || arg != floor(arg)) return Double.NaN
            var res = 1.0
            for (i in 1..arg.toInt()) res *= i
            return res
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)
        
        layoutCalculator = view.findViewById(R.id.layoutCalculator)
        tvDisplay = view.findViewById(R.id.tvDisplay)
        tvFormula = view.findViewById(R.id.tvFormula)
        scientificGrid = view.findViewById(R.id.scientificGrid)
        imgToggle = view.findViewById(R.id.imgToggle)
        mainGrid = view.findViewById(R.id.mainGrid)

        view.findViewById<View>(R.id.layoutToggle).setOnClickListener { toggleScientific() }

        val numberButtons = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9)
        for (id in numberButtons) {
            view.findViewById<Button>(id).setOnClickListener { onDigit((it as Button).text.toString()) }
        }

        view.findViewById<Button>(R.id.btnDot).setOnClickListener { onDot() }
        view.findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperator("+") }
        view.findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperator("-") }
        view.findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperator("×") }
        view.findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperator("÷") }
        view.findViewById<Button>(R.id.btnPercent).setOnClickListener { onOperator("%") }
        view.findViewById<Button>(R.id.btnPower).setOnClickListener { onOperator("^") }

        view.findViewById<Button>(R.id.btnSin).setOnClickListener { onFunction(if(isInv) "asin(" else "sin(") }
        view.findViewById<Button>(R.id.btnCos).setOnClickListener { onFunction(if(isInv) "acos(" else "cos(") }
        view.findViewById<Button>(R.id.btnTan).setOnClickListener { onFunction(if(isInv) "atan(" else "tan(") }
        view.findViewById<Button>(R.id.btnLog).setOnClickListener { onFunction("log(") }
        view.findViewById<Button>(R.id.btnLn).setOnClickListener { onFunction("ln(") }
        view.findViewById<Button>(R.id.btnSqrt).setOnClickListener { onFunction("√(") }
        view.findViewById<Button>(R.id.btnFact).setOnClickListener { 
            if(lastNumeric) { tvDisplay.append("!"); lastNumeric = false; vibrate(); calculateLive() }
        }
        view.findViewById<Button>(R.id.btnPi).setOnClickListener { onDigit("π") }
        view.findViewById<Button>(R.id.btnE).setOnClickListener { onDigit("e") }
        view.findViewById<Button>(R.id.btnRad).setOnClickListener {
            isRadians = !isRadians
            (it as Button).text = if (isRadians) "Rad" else "Deg"
            vibrate()
            calculateLive()
        }
        view.findViewById<Button>(R.id.btnInv).setOnClickListener {
            isInv = !isInv; vibrate(); updateInvButtons(view)
        }
        view.findViewById<Button>(R.id.btnBrackets).setOnClickListener { onBrackets() }
        view.findViewById<Button>(R.id.btnClear).setOnClickListener {
            tvDisplay.text = "0"; tvFormula.text = ""; lastNumeric = false; stateError = false; lastDot = false; bracketCount = 0; vibrate()
        }
        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val text = tvDisplay.text.toString()
            if (text.isNotEmpty() && text != "0") {
                if (text.endsWith("(")) {
                    bracketCount--
                    val functions = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(", "asin(", "acos(", "atan(")
                    var deleted = false
                    for (f in functions) {
                        if (text.endsWith(f)) {
                            tvDisplay.text = text.substring(0, text.length - f.length)
                            deleted = true
                            break
                        }
                    }
                    if (!deleted) tvDisplay.text = text.substring(0, text.length - 1)
                } else if (text.endsWith(")")) {
                    bracketCount++
                    tvDisplay.text = text.substring(0, text.length - 1)
                } else {
                    tvDisplay.text = text.substring(0, text.length - 1)
                }
                
                if (tvDisplay.text.isEmpty()) tvDisplay.text = "0"
                formatDisplay()
                calculateLive()
            }
            vibrate()
        }
        view.findViewById<Button>(R.id.btnEqual).setOnClickListener { onEqual() }

        // Initial setup for heights
        Handler(Looper.getMainLooper()).post { updateButtonHeights() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_main))
    }

    private fun updateInvButtons(view: View) {
        view.findViewById<Button>(R.id.btnSin).text = if(isInv) "sin⁻¹" else "sin"
        view.findViewById<Button>(R.id.btnCos).text = if(isInv) "cos⁻¹" else "cos"
        view.findViewById<Button>(R.id.btnTan).text = if(isInv) "tan⁻¹" else "tan"
    }

    private fun toggleScientific() {
        isScientificExpanded = !isScientificExpanded
        val transition = AutoTransition()
        transition.duration = 200
        TransitionManager.beginDelayedTransition(layoutCalculator, transition)
        scientificGrid.visibility = if (isScientificExpanded) View.VISIBLE else View.GONE
        imgToggle.rotation = if (isScientificExpanded) 0f else 180f
        updateButtonHeights()
        vibrate()
    }

    private fun updateButtonHeights() {
        val buttonHeight = if (isScientificExpanded) 42 else 70
        val btnTextSizeSci = if (isScientificExpanded) 16f else 22f
        val btnTextSizeMain = if (isScientificExpanded) 22f else 32f
        setGridButtonsStyle(scientificGrid, buttonHeight, btnTextSizeSci)
        setGridButtonsStyle(mainGrid, buttonHeight, btnTextSizeMain)
    }

    private fun setGridButtonsStyle(grid: GridLayout, heightDp: Int, textSizeSp: Float) {
        val heightPx = (heightDp * resources.displayMetrics.density).toInt()
        for (i in 0 until grid.childCount) {
            val child = grid.getChildAt(i)
            if (child is Button) {
                val params = child.layoutParams
                params.height = heightPx
                child.layoutParams = params
                child.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
            }
        }
    }

    private fun onDigit(digit: String) {
        if (stateError || tvDisplay.text.toString() == "0") { tvDisplay.text = digit; stateError = false }
        else tvDisplay.append(digit)
        lastNumeric = true
        formatDisplay()
        calculateLive()
        vibrate()
    }

    private fun formatDisplay() {
        val original = tvDisplay.text.toString().replace(",", "")
        if (original.isEmpty()) return
        val df = DecimalFormat("#,###.########")
        val regex = "(\\d+\\.?\\d*)".toRegex()
        val formatted = regex.replace(original) { match ->
            val numStr = match.value
            try {
                if (numStr.contains(".")) {
                    val parts = numStr.split(".")
                    val formattedInt = df.format(parts[0].toDouble())
                    "$formattedInt.${parts[1]}"
                } else {
                    df.format(numStr.toDouble())
                }
            } catch (e: Exception) { numStr }
        }
        tvDisplay.text = formatted
    }

    private fun onDot() {
        val current = tvDisplay.text.toString().replace(",", "")
        if (stateError) { tvDisplay.text = "0."; stateError = false; lastNumeric = false; lastDot = true }
        else if (!lastDot) {
            if (current == "0" || !lastNumeric) tvDisplay.append(if(current == "0") "." else "0.")
            else tvDisplay.append(".")
            lastNumeric = false; lastDot = true
        }
        calculateLive()
        vibrate()
    }

    private fun onOperator(op: String) {
        if (!stateError) {
            val currentText = tvDisplay.text.toString()
            if (op == "%") {
                tvDisplay.append("%")
                lastNumeric = false
                lastDot = false
                calculateLive()
                vibrate()
                return
            }
            if (lastNumeric || op == "-" || currentText.endsWith(")") || currentText.endsWith("π") || currentText.endsWith("e") || currentText.endsWith("%")) {
                tvDisplay.append(op); lastNumeric = false; lastDot = false; vibrate(); calculateLive()
            }
        }
    }

    private fun onFunction(func: String) {
        if (stateError || tvDisplay.text.toString() == "0") { tvDisplay.text = func; stateError = false }
        else tvDisplay.append(func)
        bracketCount++; lastNumeric = false; lastDot = false; vibrate()
    }

    private fun onBrackets() {
        val text = tvDisplay.text.toString()
        if (stateError || text == "0") { tvDisplay.text = "("; bracketCount++; stateError = false }
        else {
            if (lastNumeric || text.endsWith(")") || text.endsWith("π") || text.endsWith("e")) {
                if (bracketCount > 0) { tvDisplay.append(")"); bracketCount-- }
                else { tvDisplay.append("×("); bracketCount++ }
            } else { tvDisplay.append("("); bracketCount++ }
        }
        lastNumeric = false; lastDot = false; vibrate(); calculateLive()
    }

    private fun calculateLive() {
        var txt = tvDisplay.text.toString().replace(",", "")
        if (txt == "0" || txt.isEmpty()) {
            tvFormula.text = ""
            return
        }

        // Handle standalone percentage
        if (txt.endsWith("%") && !txt.dropLast(1).any { it in "+-×÷" }) {
            try {
                val numStr = txt.dropLast(1)
                val num = numStr.toDouble()
                tvFormula.text = DecimalFormat("#,###.########").format(num / 100.0)
                return
            } catch (e: Exception) {
                tvFormula.text = ""
                return
            }
        }

        var tempBracketCount = bracketCount
        var tempTxt = txt
        while (tempTxt.isNotEmpty() && (tempTxt.endsWith("+") || tempTxt.endsWith("-") || tempTxt.endsWith("×") || tempTxt.endsWith("÷") || tempTxt.endsWith("^") || tempTxt.endsWith("("))) {
            if (tempTxt.endsWith("(")) tempBracketCount--
            tempTxt = tempTxt.dropLast(1)
        }
        if (tempTxt.isEmpty()) { tvFormula.text = ""; return }
        repeat(max(0, tempBracketCount)) { tempTxt += ")" }

        try {
            val processed = processPercentage(tempTxt)
            val evalTxt = processed
                .replace("×", "*").replace("÷", "/").replace("−", "-")
                .replace("π", "pi").replace("e", "e").replace("√", "sqrt")

            var finalEval = evalTxt
            val factorialRegex = "(\\d+(\\.\\d+)?|pi|e|\\))!".toRegex()
            while (finalEval.contains("!")) {
                val match = factorialRegex.find(finalEval)
                if (match != null) {
                    val num = match.groupValues[1]
                    finalEval = finalEval.replaceFirst("${num}!", "fact($num)")
                } else break
            }
            finalEval = finalEval.replace("(\\d|pi|e|\\))(pi|e|sqrt|sin|cos|tan|log|ln|fact|\\()".toRegex(), "$1*$2")
            finalEval = finalEval.replace("(pi|e|\\))(\\d)".toRegex(), "$1*$2")
            finalEval = finalEval.replace("log(", "log10(").replace("ln(", "log(")

            val expression = ExpressionBuilder(finalEval).variables("pi", "e").function(factorialFunc).build().setVariable("pi", Math.PI).setVariable("e", Math.E)
            val result = expression.evaluate()
            
            if (result.isFinite()) {
                val df = DecimalFormat("#,###.########")
                tvFormula.text = df.format(result)
            }
        } catch (e: Exception) {
            tvFormula.text = ""
        }
    }

    private fun processPercentage(txt: String): String {
        // Handle "X + Y%" or "X - Y%"
        val addSubPercentRegex = "(\\d+\\.?\\d*)\\s*([+\\-])\\s*(\\d+\\.?\\d*)%".toRegex()
        var processed = addSubPercentRegex.replace(txt) { match ->
            val num1 = match.groupValues[1]
            val op = match.groupValues[2]
            val num2 = match.groupValues[3]
            "$num1$op($num1*($num2/100))"
        }
        // Handle "X * Y%" or "X / Y%"
        val mulDivPercentRegex = "(\\d+\\.?\\d*)\\s*([×÷*\\/])\\s*(\\d+\\.?\\d*)%".toRegex()
        processed = mulDivPercentRegex.replace(processed) { match ->
            val num1 = match.groupValues[1]
            val op = match.groupValues[2]
            val num2 = match.groupValues[3]
            val opChar = if (op == "×" || op == "*") "*" else "/"
            "$num1$opChar($num2/100)"
        }
        return processed.replace("(\\d+\\.?\\d*)%".toRegex(), "($1/100)")
    }

    private fun onEqual() {
        if (stateError) return
        var txt = tvDisplay.text.toString().replace(",", "")
        if (txt == "0" || txt.isEmpty()) return
        val originalFormula = tvDisplay.text.toString()
        
        while (txt.isNotEmpty() && (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("×") || txt.endsWith("÷") || txt.endsWith("^") || txt.endsWith("("))) {
            if (txt.endsWith("(")) bracketCount--
            txt = txt.substring(0, txt.length - 1)
        }
        if (txt.isEmpty()) { tvDisplay.text = "0"; return }
        repeat(max(0, bracketCount)) { txt += ")" }
        
        val processedTxt = processPercentage(txt)
        var evalTxt = processedTxt
            .replace("×", "*").replace("÷", "/").replace("−", "-")
            .replace("π", "pi").replace("e", "e").replace("√", "sqrt")

        val factorialRegex = "(\\d+(\\.\\d+)?|pi|e|\\))!".toRegex()
        while (evalTxt.contains("!")) {
            val match = factorialRegex.find(evalTxt)
            if (match != null) {
                val num = match.groupValues[1]
                evalTxt = evalTxt.replaceFirst("${num}!", "fact($num)")
            } else break
        }

        evalTxt = evalTxt.replace("(\\d|pi|e|\\))(pi|e|sqrt|sin|cos|tan|log|ln|fact|\\()".toRegex(), "$1*$2")
        evalTxt = evalTxt.replace("(pi|e|\\))(\\d)".toRegex(), "$1*$2")
        evalTxt = evalTxt.replace("log(", "log10(").replace("ln(", "log(")

        try {
            val expression = ExpressionBuilder(evalTxt).variables("pi", "e").function(factorialFunc).build().setVariable("pi", Math.PI).setVariable("e", Math.E)
            var result = expression.evaluate()
            if (result.isFinite()) {
                val rounded = BigDecimal(result).setScale(12, RoundingMode.HALF_UP).toDouble()
                if (abs(result - rounded) < 1e-13) result = rounded
            }
            val df = DecimalFormat("#,###.########")
            val resultStr = df.format(result)
            
            tvDisplay.text = resultStr
            tvFormula.text = ""
            
            (activity as? MainActivity)?.addToHistory(originalFormula, resultStr)
            lastNumeric = true; lastDot = resultStr.contains("."); bracketCount = 0
        } catch (e: Exception) {
            tvFormula.text = "Error"
            stateError = true; lastNumeric = false
        }
        vibrate()
    }

    private fun vibrate() {
        VibrationHelper.vibrate(requireContext(), view)
    }
}
