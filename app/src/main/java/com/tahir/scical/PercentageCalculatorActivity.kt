package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat

class PercentageCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvValueX: TextView
    private lateinit var tvValueY: TextView
    private lateinit var tvLabelX: TextView
    private lateinit var tvLabelY: TextView
    private lateinit var tvResultValue: TextView
    private lateinit var rgPercentageMode: RadioGroup

    private var activeField = 1 // 1: X, 2: Y
    private var xInput = "0"
    private var yInput = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_percentage_calculator)

        tvValueX = findViewById(R.id.tvValueX)
        tvValueY = findViewById(R.id.tvValueY)
        tvLabelX = findViewById(R.id.tvLabelX)
        tvLabelY = findViewById(R.id.tvLabelY)
        tvResultValue = findViewById(R.id.tvResultValue)
        rgPercentageMode = findViewById(R.id.rgPercentageMode)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in keypadIds) {
            findViewById<Button>(id).setOnClickListener { onKeypadClick((it as Button).text.toString()) }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            if (activeField == 1) xInput = "0" else yInput = "0"
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            if (activeField == 1) {
                xInput = if (xInput.length > 1) xInput.dropLast(1) else "0"
            } else {
                yInput = if (yInput.length > 1) yInput.dropLast(1) else "0"
            }
            updateUI()
        }

        tvValueX.setOnClickListener { activeField = 1; updateUI() }
        tvValueY.setOnClickListener { activeField = 2; updateUI() }

        rgPercentageMode.setOnCheckedChangeListener { _, _ -> updateLabels(); calculate() }

        updateLabels()
        updateUI()
    }

    private fun onKeypadClick(text: String) {
        if (activeField == 1) {
            if (text == "." && xInput.contains(".")) return
            xInput = if (xInput == "0" && text != ".") text else xInput + text
        } else {
            if (text == "." && yInput.contains(".")) return
            yInput = if (yInput == "0" && text != ".") text else yInput + text
        }
        updateUI()
    }

    private fun updateLabels() {
        when (rgPercentageMode.checkedRadioButtonId) {
            R.id.rbWhatIsXPercentOfY -> {
                tvLabelX.text = "X (Percentage %)"
                tvLabelY.text = "Y (Total Value)"
            }
            R.id.rbXIsWhatPercentOfY -> {
                tvLabelX.text = "X (Part Value)"
                tvLabelY.text = "Y (Total Value)"
            }
            R.id.rbPercentageChange -> {
                tvLabelX.text = "X (Initial Value)"
                tvLabelY.text = "Y (Final Value)"
            }
        }
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvValueX.text = xInput
        tvValueY.text = yInput

        tvValueX.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvValueY.setTextColor(if (activeField == 2) highlightColor else secondaryColor)

        calculate()
    }

    private fun calculate() {
        val x = xInput.toDoubleOrNull() ?: 0.0
        val y = yInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.##")

        val result = when (rgPercentageMode.checkedRadioButtonId) {
            R.id.rbWhatIsXPercentOfY -> (x / 100) * y
            R.id.rbXIsWhatPercentOfY -> if (y != 0.0) (x / y) * 100 else 0.0
            R.id.rbPercentageChange -> if (x != 0.0) ((y - x) / x) * 100 else 0.0
            else -> 0.0
        }

        val suffix = if (rgPercentageMode.checkedRadioButtonId != R.id.rbWhatIsXPercentOfY) "%" else ""
        tvResultValue.text = df.format(result) + suffix
    }
}
