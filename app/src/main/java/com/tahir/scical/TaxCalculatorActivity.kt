package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat

class TaxCalculatorActivity : AppCompatActivity() {

    private lateinit var tvIncomeAmount: TextView
    private lateinit var tvDeductions: TextView
    private lateinit var cardResult: MaterialCardView
    private lateinit var tvTaxableIncome: TextView
    private lateinit var tvResultTax: TextView
    private lateinit var tvResultNet: TextView

    private var activeField = 1 // 1: Income, 2: Deductions
    private var incomeInput = "1200000"
    private var deductionsInput = "150000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tax_calculator)

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvIncomeAmount = findViewById(R.id.tvIncomeAmount)
        tvDeductions = findViewById(R.id.tvDeductions)
        cardResult = findViewById(R.id.cardResult)
        tvTaxableIncome = findViewById(R.id.tvTaxableIncome)
        tvResultTax = findViewById(R.id.tvResultTax)
        tvResultNet = findViewById(R.id.tvResultNet)

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

        tvIncomeAmount.setOnClickListener { activeField = 1; updateUI() }
        tvDeductions.setOnClickListener { activeField = 2; updateUI() }

        findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateTax() }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        val currentStr = if (activeField == 1) incomeInput else deductionsInput
        if (text == "." && currentStr.contains(".")) return
        val newStr = if (currentStr == "0" && text != ".") text else currentStr + text
        if (activeField == 1) incomeInput = newStr else deductionsInput = newStr
        updateUI()
    }

    private fun resetCurrentField() {
        if (activeField == 1) incomeInput = "0" else deductionsInput = "0"
    }

    private fun deleteLastChar() {
        if (activeField == 1) {
            incomeInput = if (incomeInput.length > 1) incomeInput.dropLast(1) else "0"
        } else {
            deductionsInput = if (deductionsInput.length > 1) deductionsInput.dropLast(1) else "0"
        }
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvIncomeAmount.text = incomeInput
        tvDeductions.text = deductionsInput

        tvIncomeAmount.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvDeductions.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
    }

    private fun calculateTax() {
        val income = incomeInput.toDoubleOrNull() ?: 0.0
        val deductions = deductionsInput.toDoubleOrNull() ?: 0.0
        val taxableIncome = maxOf(0.0, income - deductions)

        // Simplified Indian Income Tax Slabs (Old Regime example)
        var tax = 0.0
        if (taxableIncome > 250000) {
            if (taxableIncome <= 500000) {
                tax += (taxableIncome - 250000) * 0.05
            } else if (taxableIncome <= 1000000) {
                tax += 12500 + (taxableIncome - 500000) * 0.20
            } else {
                tax += 12500 + 100000 + (taxableIncome - 1000000) * 0.30
            }
        }
        
        // Add 4% Cess
        tax *= 1.04

        val netTakeHome = income - tax
        val df = DecimalFormat("#,##,###.##")

        tvTaxableIncome.text = df.format(taxableIncome)
        tvResultTax.text = df.format(tax)
        tvResultNet.text = df.format(netTakeHome)
        cardResult.visibility = View.VISIBLE
    }
}
