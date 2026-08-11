package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class UnitPriceCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvPrice1: TextView
    private lateinit var tvQty1: TextView
    private lateinit var tvUnit1: TextView
    private lateinit var tvPrice2: TextView
    private lateinit var tvQty2: TextView
    private lateinit var tvUnit2: TextView
    private lateinit var tvVerdict: TextView
    private lateinit var tvCurrencySymbol: TextView
    private lateinit var cardProduct1: MaterialCardView
    private lateinit var cardProduct2: MaterialCardView

    private var activeField = 1 // 1: Price1, 2: Qty1, 3: Price2, 4: Qty2
    private var price1Input = "0"
    private var qty1Input = "0"
    private var price2Input = "0"
    private var qty2Input = "0"
    
    private var unit1Factor = 1.0 // Grams as base
    private var unit2Factor = 1.0
    private var currentCurrency = "INR"

    private val units = arrayOf(
        ItemUnit("Gram", "g", 1.0),
        ItemUnit("Kilogram", "kg", 1000.0),
        ItemUnit("Milligram", "mg", 0.001),
        ItemUnit("Pound", "lb", 453.592),
        ItemUnit("Ounce", "oz", 28.3495),
        ItemUnit("Milliliter", "mL", 1.0),
        ItemUnit("Liter", "L", 1000.0),
        ItemUnit("Fluid Ounce", "fl oz", 29.5735),
        ItemUnit("Gallon", "gal", 3785.41),
        ItemUnit("Piece/Unit", "pcs", 1.0)
    )

    private val currencies = arrayOf("INR", "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "AED", "SAR", "CNY")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_price_calculator)

        tvPrice1 = findViewById(R.id.tvPrice1)
        tvQty1 = findViewById(R.id.tvQty1)
        tvUnit1 = findViewById(R.id.tvUnit1)
        tvPrice2 = findViewById(R.id.tvPrice2)
        tvQty2 = findViewById(R.id.tvQty2)
        tvUnit2 = findViewById(R.id.tvUnit2)
        tvVerdict = findViewById(R.id.tvVerdict)
        tvCurrencySymbol = findViewById(R.id.tvCurrencySymbol)
        cardProduct1 = findViewById(R.id.cardProduct1)
        cardProduct2 = findViewById(R.id.cardProduct2)

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

        findViewById<View>(R.id.layoutPrice1).setOnClickListener { activeField = 1; updateUI() }
        findViewById<View>(R.id.layoutQty1).setOnClickListener { activeField = 2; updateUI() }
        findViewById<View>(R.id.layoutPrice2).setOnClickListener { activeField = 3; updateUI() }
        findViewById<View>(R.id.layoutQty2).setOnClickListener { activeField = 4; updateUI() }

        tvUnit1.setOnClickListener { showUnitPicker(true) }
        tvUnit2.setOnClickListener { showUnitPicker(false) }
        tvCurrencySymbol.setOnClickListener { showCurrencyPicker() }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        val currentStr = when (activeField) {
            1 -> price1Input
            2 -> qty1Input
            3 -> price2Input
            4 -> qty2Input
            else -> "0"
        }

        if (text == "." && currentStr.contains(".")) return
        
        val newStr = if (currentStr == "0" && text != ".") text else currentStr + text
        
        when (activeField) {
            1 -> price1Input = newStr
            2 -> qty1Input = newStr
            3 -> price2Input = newStr
            4 -> qty2Input = newStr
        }
        updateUI()
    }

    private fun resetCurrentField() {
        when (activeField) {
            1 -> price1Input = "0"
            2 -> qty1Input = "0"
            3 -> price2Input = "0"
            4 -> qty2Input = "0"
        }
    }

    private fun deleteLastChar() {
        when (activeField) {
            1 -> price1Input = if (price1Input.length > 1) price1Input.dropLast(1) else "0"
            2 -> qty1Input = if (qty1Input.length > 1) qty1Input.dropLast(1) else "0"
            3 -> price2Input = if (price2Input.length > 1) price2Input.dropLast(1) else "0"
            4 -> qty2Input = if (qty2Input.length > 1) qty2Input.dropLast(1) else "0"
        }
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvPrice1.text = price1Input
        tvQty1.text = qty1Input
        tvPrice2.text = price2Input
        tvQty2.text = qty2Input
        tvCurrencySymbol.text = currentCurrency

        tvPrice1.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvQty1.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        tvPrice2.setTextColor(if (activeField == 3) highlightColor else secondaryColor)
        tvQty2.setTextColor(if (activeField == 4) highlightColor else secondaryColor)

        // Visual cues for active product
        cardProduct1.strokeColor = if (activeField <= 2) highlightColor else "#E0E0E0".toColorInt()
        cardProduct2.strokeColor = if (activeField >= 3) highlightColor else "#E0E0E0".toColorInt()

        calculateBestDeal()
    }

    private fun calculateBestDeal() {
        val p1 = price1Input.toDoubleOrNull() ?: 0.0
        val q1 = (qty1Input.toDoubleOrNull() ?: 0.0) * unit1Factor
        val p2 = price2Input.toDoubleOrNull() ?: 0.0
        val q2 = (qty2Input.toDoubleOrNull() ?: 0.0) * unit2Factor

        if (p1 > 0 && q1 > 0 && p2 > 0 && q2 > 0) {
            val unitPrice1 = p1 / q1
            val unitPrice2 = p2 / q2
            
            val df = DecimalFormat("#.##")
            if (unitPrice1 < unitPrice2) {
                val diff = ((unitPrice2 - unitPrice1) / unitPrice2) * 100
                tvVerdict.text = "Product 1 is cheaper by ${df.format(diff)}%"
                tvVerdict.setTextColor("#00C09A".toColorInt())
            } else if (unitPrice2 < unitPrice1) {
                val diff = ((unitPrice1 - unitPrice2) / unitPrice1) * 100
                tvVerdict.text = "Product 2 is cheaper by ${df.format(diff)}%"
                tvVerdict.setTextColor("#00C09A".toColorInt())
            } else {
                tvVerdict.text = "Both products have the same unit price"
                tvVerdict.setTextColor("#70757A".toColorInt())
            }
        } else {
            tvVerdict.text = "Enter values to compare"
            tvVerdict.setTextColor("#7F00FF".toColorInt())
        }
    }

    private fun showUnitPicker(isProduct1: Boolean) {
        val names = units.map { "${it.name} (${it.symbol})" }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(names) { _, which ->
                val selected = units[which]
                if (isProduct1) {
                    tvUnit1.text = selected.symbol
                    unit1Factor = selected.factor
                } else {
                    tvUnit2.text = selected.symbol
                    unit2Factor = selected.factor
                }
                updateUI()
            }.show()
    }

    private fun showCurrencyPicker() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Currency")
            .setItems(currencies) { _, which ->
                currentCurrency = currencies[which]
                updateUI()
            }.show()
    }

    data class ItemUnit(val name: String, val symbol: String, val factor: Double)
}
