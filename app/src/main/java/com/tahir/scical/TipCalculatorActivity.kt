package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat

class TipCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvBillAmount: TextView
    private lateinit var tvTipPercent: TextView
    private lateinit var tvPeopleCount: TextView
    private lateinit var tvTotalTip: TextView
    private lateinit var tvTotalBill: TextView
    private lateinit var tvPerPerson: TextView

    private var activeField = 1 // 1: Bill, 2: Tip%, 3: People
    private var billInput = "0"
    private var tipInput = "15"
    private var peopleInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_calculator)

        tvBillAmount = findViewById(R.id.tvBillAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvPeopleCount = findViewById(R.id.tvPeopleCount)
        tvTotalTip = findViewById(R.id.tvTotalTip)
        tvTotalBill = findViewById(R.id.tvTotalBill)
        tvPerPerson = findViewById(R.id.tvPerPerson)

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

        tvBillAmount.setOnClickListener { activeField = 1; updateUI() }
        tvTipPercent.setOnClickListener { activeField = 2; updateUI() }
        tvPeopleCount.setOnClickListener { activeField = 3; updateUI() }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        val currentStr = when (activeField) {
            1 -> billInput
            2 -> tipInput
            3 -> peopleInput
            else -> "0"
        }

        if (text == "." && (currentStr.contains(".") || activeField == 3)) return
        
        val newStr = if (currentStr == "0" && text != ".") text else if (currentStr == "0" && text == ".") "0." else currentStr + text
        
        when (activeField) {
            1 -> billInput = newStr
            2 -> tipInput = newStr
            3 -> peopleInput = newStr
        }
        updateUI()
    }

    private fun resetCurrentField() {
        when (activeField) {
            1 -> billInput = "0"
            2 -> tipInput = "0"
            3 -> peopleInput = "0"
        }
    }

    private fun deleteLastChar() {
        when (activeField) {
            1 -> billInput = if (billInput.length > 1) billInput.dropLast(1) else "0"
            2 -> tipInput = if (tipInput.length > 1) tipInput.dropLast(1) else "0"
            3 -> peopleInput = if (peopleInput.length > 1) peopleInput.dropLast(1) else "0"
        }
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvBillAmount.text = billInput
        tvTipPercent.text = tipInput
        tvPeopleCount.text = peopleInput

        tvBillAmount.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvTipPercent.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        tvPeopleCount.setTextColor(if (activeField == 3) highlightColor else secondaryColor)

        calculate()
    }

    private fun calculate() {
        val bill = billInput.toDoubleOrNull() ?: 0.0
        val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
        val people = peopleInput.toIntOrNull() ?: 0

        val totalTip = bill * (tipPercent / 100)
        val totalBill = bill + totalTip
        val perPerson = if (people > 0) totalBill / people else 0.0

        val df = DecimalFormat("#,###.##")
        tvTotalTip.text = df.format(totalTip)
        tvTotalBill.text = df.format(totalBill)
        tvPerPerson.text = df.format(perPerson)
    }
}
