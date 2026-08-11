package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrencyCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView
    private lateinit var tvUnit3Symbol: TextView
    private lateinit var tvUnit3Value: TextView
    private lateinit var tvUnit3Name: TextView
    private lateinit var btnRefreshRates: ImageView

    private var activeField = 1
    private var unit1Rate = 1.0 
    private var unit2Rate = 84.40 // Highly accurate default
    private var unit3Rate = 91.50 // Highly accurate default

    private val currencies = arrayOf(
        Currency("Indian Rupee", "INR", 1.0),
        Currency("United States Dollar", "USD", 84.40),
        Currency("Euro", "EUR", 91.50),
        Currency("British Pound", "GBP", 107.25),
        Currency("Japanese Yen", "JPY", 0.558),
        Currency("Australian Dollar", "AUD", 55.30),
        Currency("Canadian Dollar", "CAD", 61.80),
        Currency("Swiss Franc", "CHF", 95.20),
        Currency("Chinese Yuan", "CNY", 11.60),
        Currency("UAE Dirham", "AED", 22.98),
        Currency("Kuwaiti Dinar", "KWD", 274.40),
        Currency("Saudi Riyal", "SAR", 22.50),
        Currency("Omani Rial", "OMR", 219.20),
        Currency("Qatari Rial", "QAR", 23.18),
        Currency("Bahraini Dinar", "BHD", 223.90),
        Currency("Singapore Dollar", "SGD", 62.45),
        Currency("New Zealand Dollar", "NZD", 50.80),
        Currency("Hong Kong Dollar", "HKD", 10.80),
        Currency("South African Rand", "ZAR", 4.65),
        Currency("Russian Ruble", "RUB", 0.92),
        Currency("Brazilian Real", "BRL", 16.20),
        Currency("South Korean Won", "KRW", 0.061),
        Currency("Mexican Peso", "MXN", 4.15),
        Currency("Thai Baht", "THB", 2.48),
        Currency("Indonesian Rupiah", "IDR", 0.0054),
        Currency("Turkish Lira", "TRY", 2.45),
        Currency("Malaysian Ringgit", "MYR", 18.90),
        Currency("Philippine Peso", "PHP", 1.45)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_calculator)

        tvUnit1Symbol = findViewById(R.id.tvUnit1Symbol)
        tvUnit1Value = findViewById(R.id.tvUnit1Value)
        tvUnit1Name = findViewById(R.id.tvUnit1Name)
        tvUnit2Symbol = findViewById(R.id.tvUnit2Symbol)
        tvUnit2Value = findViewById(R.id.tvUnit2Value)
        tvUnit2Name = findViewById(R.id.tvUnit2Name)
        tvUnit3Symbol = findViewById(R.id.tvUnit3Symbol)
        tvUnit3Value = findViewById(R.id.tvUnit3Value)
        tvUnit3Name = findViewById(R.id.tvUnit3Name)
        btnRefreshRates = findViewById(R.id.btnRefreshRates)

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
            currentInput = "0"
            tvUnit1Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            tvUnit2Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            tvUnit3Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            updateValues()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
            updateValues()
        }

        btnRefreshRates.setOnClickListener {
            fetchLiveRates()
        }

        // Unit Selectors
        tvUnit1Symbol.setOnClickListener {
            activeField = 1
            showCurrencyPicker { selected ->
                tvUnit1Symbol.text = selected.symbol
                tvUnit1Name.text = selected.name
                unit1Rate = selected.rateToInr
                updateValues()
            }
        }
        tvUnit1Value.setOnClickListener {
            activeField = 1
            currentInput = tvUnit1Value.text.toString().replace(",", "")
            updateValues()
        }

        tvUnit2Symbol.setOnClickListener {
            activeField = 2
            showCurrencyPicker { selected ->
                tvUnit2Symbol.text = selected.symbol
                tvUnit2Name.text = selected.name
                unit2Rate = selected.rateToInr
                updateValues()
            }
        }
        tvUnit2Value.setOnClickListener {
            activeField = 2
            currentInput = tvUnit2Value.text.toString().replace(",", "")
            updateValues()
        }

        tvUnit3Symbol.setOnClickListener {
            activeField = 3
            showCurrencyPicker { selected ->
                tvUnit3Symbol.text = selected.symbol
                tvUnit3Name.text = selected.name
                unit3Rate = selected.rateToInr
                updateValues()
            }
        }
        tvUnit3Value.setOnClickListener {
            activeField = 3
            currentInput = tvUnit3Value.text.toString().replace(",", "")
            updateValues()
        }

        fetchLiveRates()
        updateValues()
    }

    private fun fetchLiveRates() {
        val savedValue = if (currentInput == "0") "1" else currentInput
        
        btnRefreshRates.animate().rotationBy(360f).setDuration(500).start()
        
        Thread {
            try {
                // Using standard exchange-rate API
                val url = URL("https://api.exchangerate-api.com/v4/latest/INR")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                val data = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(data)
                
                val rates = json.getJSONObject("rates")

                runOnUiThread {
                    for (currency in currencies) {
                        if (rates.has(currency.symbol)) {
                            val rateFromInr = rates.getDouble(currency.symbol)
                            currency.rateToInr = 1.0 / rateFromInr
                        }
                    }
                    updateActiveRates()
                    
                    // Restore and recalculate with new rates
                    currentInput = savedValue
                    updateValues()
                    Toast.makeText(this, "Rates updated successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    // Even on error, restore the previous value
                    currentInput = savedValue
                    updateValues()
                    Toast.makeText(this, "Failed to update rates", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }.start()
    }

    private fun updateActiveRates() {
        currencies.find { it.symbol == tvUnit1Symbol.text }?.let { unit1Rate = it.rateToInr }
        currencies.find { it.symbol == tvUnit2Symbol.text }?.let { unit2Rate = it.rateToInr }
        currencies.find { it.symbol == tvUnit3Symbol.text }?.let { unit3Rate = it.rateToInr }
    }

    private fun onKeypadClick(text: String) {
        if (text == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0" && text != ".") text else currentInput + text
        updateValues()
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

    private fun updateValues() {
        val value = currentInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.####")
        val highlightColor = "#7F00FF".toColorInt()
        
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) {
            getColor(typedValue.resourceId)
        } else {
            typedValue.data
        }

        // Reset all colors
        tvUnit1Value.setTextColor(secondaryColor)
        tvUnit2Value.setTextColor(secondaryColor)
        tvUnit3Value.setTextColor(secondaryColor)

        when (activeField) {
            1 -> {
                tvUnit1Value.text = formatNumber(currentInput)
                tvUnit1Value.setTextColor(highlightColor)
                val inr = value * unit1Rate
                tvUnit2Value.text = df.format(inr / unit2Rate)
                tvUnit3Value.text = df.format(inr / unit3Rate)
            }
            2 -> {
                tvUnit2Value.text = formatNumber(currentInput)
                tvUnit2Value.setTextColor(highlightColor)
                val inr = value * unit2Rate
                tvUnit1Value.text = df.format(inr / unit1Rate)
                tvUnit3Value.text = df.format(inr / unit3Rate)
            }
            3 -> {
                tvUnit3Value.text = formatNumber(currentInput)
                tvUnit3Value.setTextColor(highlightColor)
                val inr = value * unit3Rate
                tvUnit1Value.text = df.format(inr / unit1Rate)
                tvUnit2Value.text = df.format(inr / unit2Rate)
            }
        }
    }

    private fun showCurrencyPicker(onSelected: (Currency) -> Unit) {
        val sortedCurrencies = currencies.sortedBy { it.name }
        val names = sortedCurrencies.map { "${it.symbol} - ${it.name}" }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Currency")
            .setItems(names) { _, which ->
                onSelected(sortedCurrencies[which])
            }
            .show()
    }

    data class Currency(val name: String, val symbol: String, var rateToInr: Double)
}
