package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat
import kotlin.math.pow

class FdCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvFdAmount: TextView
    private lateinit var tvInterestRate: TextView
    private lateinit var tvDuration: TextView
    private lateinit var cardResult: MaterialCardView
    private lateinit var tvResultInvested: TextView
    private lateinit var tvResultReturns: TextView
    private lateinit var tvResultTotal: TextView

    private var activeField = 1 // 1: Amount, 2: Rate, 3: Duration
    private var amountInput = "10000"
    private var rateInput = "6.5"
    private var durationInput = "5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fd_calculator)

        tvFdAmount = findViewById(R.id.tvFdAmount)
        tvInterestRate = findViewById(R.id.tvInterestRate)
        tvDuration = findViewById(R.id.tvDuration)
        cardResult = findViewById(R.id.cardResult)
        tvResultInvested = findViewById(R.id.tvResultInvested)
        tvResultReturns = findViewById(R.id.tvResultReturns)
        tvResultTotal = findViewById(R.id.tvResultTotal)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in keypadIds) {
            findViewById<Button>(id).setOnClickListener { onKeypadClick((it as Button).text.toString()) }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            resetCurrentField()
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            deleteLastChar()
            updateUI()
        }

        tvFdAmount.setOnClickListener { activeField = 1; updateUI() }
        tvInterestRate.setOnClickListener { activeField = 2; updateUI() }
        tvDuration.setOnClickListener { activeField = 3; updateUI() }

        findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateFD() }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        val currentStr = when (activeField) {
            1 -> amountInput
            2 -> rateInput
            3 -> durationInput
            else -> "0"
        }
        if (text == "." && currentStr.contains(".")) return
        val newStr = if (currentStr == "0" && text != ".") text else currentStr + text
        when (activeField) {
            1 -> amountInput = newStr
            2 -> rateInput = newStr
            3 -> durationInput = newStr
        }
        updateUI()
    }

    private fun resetCurrentField() {
        when (activeField) {
            1 -> amountInput = "0"
            2 -> rateInput = "0"
            3 -> durationInput = "0"
        }
    }

    private fun deleteLastChar() {
        when (activeField) {
            1 -> amountInput = if (amountInput.length > 1) amountInput.dropLast(1) else "0"
            2 -> rateInput = if (rateInput.length > 1) rateInput.dropLast(1) else "0"
            3 -> durationInput = if (durationInput.length > 1) durationInput.dropLast(1) else "0"
        }
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvFdAmount.text = amountInput
        tvInterestRate.text = rateInput
        tvDuration.text = durationInput

        tvFdAmount.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvInterestRate.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        tvDuration.setTextColor(if (activeField == 3) highlightColor else secondaryColor)
    }

    private fun calculateFD() {
        val P = amountInput.toDoubleOrNull() ?: 0.0
        val r = (rateInput.toDoubleOrNull() ?: 0.0) / 100
        val t = durationInput.toDoubleOrNull() ?: 0.0

        if (P <= 0 || r <= 0 || t <= 0) {
            Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
            return
        }

        // Standard FD Formula (Quarterly Compounding)
        val n = 4.0 // compounded quarterly
        val totalAmount = P * (1 + r / n).pow(n * t)
        val estReturns = totalAmount - P

        val df = DecimalFormat("#,##,###.##")
        tvResultInvested.text = df.format(P)
        tvResultReturns.text = df.format(estReturns)
        tvResultTotal.text = df.format(totalAmount)
        cardResult.visibility = View.VISIBLE
    }
}
