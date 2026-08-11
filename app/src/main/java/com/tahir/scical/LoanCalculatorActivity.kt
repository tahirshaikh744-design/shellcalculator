package com.tahir.scical

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat
import kotlin.math.pow

class LoanCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvLoanAmount: TextView
    private lateinit var tvInterestRate: TextView
    private lateinit var tvDurationDisplay: TextView

    private var activeField = 1 // 1: Amount, 2: Rate
    private var amountInput = "0"
    private var rateInput = "0"
    private var selectedYears = 1
    private var selectedMonths = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_calculator)

        tvLoanAmount = findViewById(R.id.tvLoanAmount)
        tvInterestRate = findViewById(R.id.tvInterestRate)
        tvDurationDisplay = findViewById(R.id.tvDurationDisplay)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in keypadIds) {
            findViewById<Button>(id).setOnClickListener { 
                onKeypadClick((it as Button).text.toString()) 
            }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            resetCurrentField()
            tvLoanAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            tvInterestRate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            deleteLastChar()
            updateUI()
        }

        tvLoanAmount.setOnClickListener { activeField = 1; updateUI() }
        tvInterestRate.setOnClickListener { activeField = 2; updateUI() }
        findViewById<View>(R.id.layoutDuration).setOnClickListener { showDurationPicker() }

        findViewById<Button>(R.id.btnCalculate).setOnClickListener {
            calculateLoan()
        }

        updateUI()
    }

    private fun resetCurrentField() {
        when (activeField) {
            1 -> amountInput = "0"
            2 -> rateInput = "0"
        }
    }

    private fun deleteLastChar() {
        when (activeField) {
            1 -> amountInput = if (amountInput.length > 1) amountInput.dropLast(1) else "0"
            2 -> rateInput = if (rateInput.length > 1) rateInput.dropLast(1) else "0"
        }
    }

    private fun onKeypadClick(text: String) {
        when (activeField) {
            1 -> {
                if (text == "." && amountInput.contains(".")) return
                amountInput = if (amountInput == "0" && text != ".") text else amountInput + text
            }
            2 -> {
                if (text == "." && rateInput.contains(".")) return
                rateInput = if (rateInput == "0" && text != ".") text else rateInput + text
            }
        }
        updateUI()
    }

    private fun formatNumber(input: String): String {
        if (input.isEmpty() || input == "0") return input
        val parts = input.split(".")
        val df = DecimalFormat("#,###")
        val formattedInt = try {
            df.format(parts[0].toDouble())
        } catch (e: Exception) {
            parts[0]
        }
        return if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvLoanAmount.text = if (amountInput == "0") "Enter amount" else formatNumber(amountInput)
        tvInterestRate.text = if (rateInput == "0") "Enter annual rate" else formatNumber(rateInput)
        
        val durationStr = StringBuilder()
        if (selectedYears > 0) durationStr.append("$selectedYears year${if (selectedYears > 1) "s" else ""} ")
        if (selectedMonths > 0) durationStr.append("$selectedMonths month${if (selectedMonths > 1) "s" else ""}")
        if (durationStr.isEmpty()) durationStr.append("0 months")
        tvDurationDisplay.text = durationStr.toString().trim()

        tvLoanAmount.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvInterestRate.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        
        tvLoanAmount.alpha = if (amountInput == "0" && activeField != 1) 0.5f else 1.0f
        tvInterestRate.alpha = if (rateInput == "0" && activeField != 2) 0.5f else 1.0f
    }

    private fun showDurationPicker() {
        val view = layoutInflater.inflate(R.layout.dialog_duration_picker, null)
        val pickerYears = view.findViewById<NumberPicker>(R.id.pickerYears)
        val pickerMonths = view.findViewById<NumberPicker>(R.id.pickerMonths)

        pickerYears.minValue = 0; pickerYears.maxValue = 30; pickerYears.value = selectedYears
        pickerMonths.minValue = 0; pickerMonths.maxValue = 11; pickerMonths.value = selectedMonths

        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                selectedYears = pickerYears.value
                selectedMonths = pickerMonths.value
                updateUI()
            }.show()
    }

    private fun calculateLoan() {
        val P = amountInput.toDoubleOrNull() ?: 0.0
        val annualRateValue = rateInput.toDoubleOrNull() ?: 0.0
        val annualRate = annualRateValue / 100
        val totalMonths = selectedYears * 12 + selectedMonths

        if (P <= 0 || totalMonths <= 0) {
            Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = annualRate / 12
        val emi: Double
        val totalAmount: Double
        val totalInterest: Double

        if (monthlyRate > 0) {
            emi = (P * monthlyRate * (1 + monthlyRate).pow(totalMonths)) / ((1 + monthlyRate).pow(totalMonths) - 1)
            totalAmount = emi * totalMonths
            totalInterest = totalAmount - P
        } else {
            emi = P / totalMonths
            totalAmount = P
            totalInterest = 0.0
        }

        val df = DecimalFormat("#,##,###.##")
        
        val intent = Intent(this, ResultDetailsActivity::class.java).apply {
            putExtra("mainLabel", "EMI")
            putExtra("duration", tvDurationDisplay.text.toString())
            putExtra("mainValue", df.format(emi))
            putExtra("secondaryLabel", "Total payment")
            putExtra("secondaryValue", df.format(totalAmount))
            putExtra("bottomLabel1", "Total\nprincipal")
            putExtra("bottomValue1", df.format(P))
            putExtra("bottomLabel2", "Total\ninterest")
            putExtra("bottomValue2", df.format(totalInterest))
            putExtra("weight1", (P / totalAmount * 100).toFloat())
            putExtra("weight2", (totalInterest / totalAmount * 100).toFloat())
            
            // Pass raw data for stats
            putExtra("rawPrincipal", P)
            putExtra("rawRate", annualRateValue)
            putExtra("rawMonths", totalMonths)
        }
        startActivity(intent)
    }
}
