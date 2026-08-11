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

class InvestmentCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvInvestmentAmount: TextView
    private lateinit var tvInterestRate: TextView
    private lateinit var tvDurationDisplay: TextView
    private lateinit var rgInvestmentType: RadioGroup
    private lateinit var rbOneTime: RadioButton
    private lateinit var rbRecurring: RadioButton

    private var activeField = 1 // 1: Amount, 2: Rate
    private var amountInput = "5000"
    private var rateInput = "12"
    private var selectedYears = 10
    private var selectedMonths = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investment_calculator)

        tvInvestmentAmount = findViewById(R.id.tvInvestmentAmount)
        tvInterestRate = findViewById(R.id.tvInterestRate)
        tvDurationDisplay = findViewById(R.id.tvDurationDisplay)
        rgInvestmentType = findViewById(R.id.rgInvestmentType)
        rbOneTime = findViewById(R.id.rbOneTime)
        rbRecurring = findViewById(R.id.rbRecurring)

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
            if (activeField == 1) amountInput = "0" else rateInput = "0"
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            if (activeField == 1) {
                amountInput = if (amountInput.length > 1) amountInput.dropLast(1) else "0"
            } else {
                rateInput = if (rateInput.length > 1) rateInput.dropLast(1) else "0"
            }
            updateUI()
        }

        tvInvestmentAmount.setOnClickListener { activeField = 1; updateUI() }
        tvInterestRate.setOnClickListener { activeField = 2; updateUI() }
        findViewById<View>(R.id.layoutDuration).setOnClickListener { showDurationPicker() }

        findViewById<Button>(R.id.btnCalculate).setOnClickListener {
            calculateInvestment()
        }

        rgInvestmentType.setOnCheckedChangeListener { _, _ -> updateUI() }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        if (activeField == 1) {
            if (text == "." && amountInput.contains(".")) return
            amountInput = if (amountInput == "0" && text != ".") text else amountInput + text
        } else {
            if (text == "." && rateInput.contains(".")) return
            rateInput = if (rateInput == "0" && text != ".") text else rateInput + text
        }
        updateUI()
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvInvestmentAmount.text = if (amountInput == "0") "Enter amount" else amountInput
        tvInterestRate.text = if (rateInput == "0") "Enter annual rate" else rateInput
        
        val durationStr = StringBuilder()
        if (selectedYears > 0) durationStr.append("$selectedYears year${if (selectedYears > 1) "s" else ""} ")
        if (selectedMonths > 0) durationStr.append("$selectedMonths month${if (selectedMonths > 1) "s" else ""}")
        if (durationStr.isEmpty()) durationStr.append("0 months")
        tvDurationDisplay.text = durationStr.toString().trim()

        tvInvestmentAmount.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvInterestRate.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        
        tvInvestmentAmount.alpha = if (amountInput == "0" && activeField != 1) 0.5f else 1.0f
        tvInterestRate.alpha = if (rateInput == "0" && activeField != 2) 0.5f else 1.0f
    }

    private fun showDurationPicker() {
        val view = layoutInflater.inflate(R.layout.dialog_duration_picker, null)
        val pickerYears = view.findViewById<NumberPicker>(R.id.pickerYears)
        val pickerMonths = view.findViewById<NumberPicker>(R.id.pickerMonths)

        pickerYears.minValue = 0; pickerYears.maxValue = 50; pickerYears.value = selectedYears
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

    private fun calculateInvestment() {
        val P = amountInput.toDoubleOrNull() ?: 0.0
        val rValue = rateInput.toDoubleOrNull() ?: 0.0
        val r = rValue / 100
        val totalMonths = selectedYears * 12 + selectedMonths
        val t = totalMonths / 12.0

        if (P <= 0 || (selectedYears == 0 && selectedMonths == 0)) {
            Toast.makeText(this, "Please enter valid amount and duration", Toast.LENGTH_SHORT).show()
            return
        }

        val totalValue: Double
        val investedAmount: Double

        if (rbRecurring.isChecked) {
            val i = r / 12
            val n = totalMonths.toDouble()
            investedAmount = P * n
            totalValue = if (r > 0) P * (((1 + i).pow(n) - 1) / i) * (1 + i) else investedAmount
        } else {
            investedAmount = P
            totalValue = P * (1 + r).pow(t)
        }

        val estReturns = totalValue - investedAmount
        val df = DecimalFormat("#,##,###.##")

        val intent = Intent(this, ResultDetailsActivity::class.java).apply {
            putExtra("mainLabel", "Maturity Value")
            putExtra("duration", tvDurationDisplay.text.toString())
            putExtra("mainValue", df.format(totalValue))
            putExtra("secondaryLabel", "Estimated returns")
            putExtra("secondaryValue", df.format(estReturns))
            putExtra("bottomLabel1", "Invested\namount")
            putExtra("bottomValue1", df.format(investedAmount))
            putExtra("bottomLabel2", "Est.\nreturns")
            putExtra("bottomValue2", df.format(estReturns))
            putExtra("weight1", (investedAmount / totalValue * 100).toFloat())
            putExtra("weight2", (estReturns / totalValue * 100).toFloat())
            
            // PASS RAW DATA FOR STATS
            putExtra("rawPrincipal", P)
            putExtra("rawRate", rValue)
            putExtra("rawMonths", totalMonths)
            putExtra("isRecurring", rbRecurring.isChecked)
        }
        startActivity(intent)
    }
}
