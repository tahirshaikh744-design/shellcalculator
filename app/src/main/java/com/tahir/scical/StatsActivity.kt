package com.tahir.scical

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

class StatsActivity : BaseCalculatorActivity() {

    private lateinit var rvStats: RecyclerView
    private lateinit var tvHeader1: TextView
    private var isMonthly = true
    
    private var principal = 0.0
    private var annualRate = 0.0
    private var totalMonths = 0
    private var isInvestment = false
    private var isRecurring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        principal = intent.getDoubleExtra("rawPrincipal", 0.0)
        annualRate = intent.getDoubleExtra("rawRate", 0.0)
        totalMonths = intent.getIntExtra("rawMonths", 0)
        isInvestment = intent.getStringExtra("type") == "Maturity Value"
        isRecurring = intent.getBooleanExtra("isRecurring", false)

        rvStats = findViewById(R.id.rvStats)
        tvHeader1 = findViewById(R.id.tvHeader1)
        rvStats.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnShare).setOnClickListener { shareAsCsv() }

        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isMonthly = checkedId == R.id.btnMonthly
                tvHeader1.text = if (isMonthly) "Month" else "Year"
                updateList()
            }
        }

        updateList()
    }

    private fun updateList() {
        val items = if (isMonthly) calculateMonthly() else calculateYearly()
        rvStats.adapter = StatsAdapter(items)
    }

    private fun shareAsCsv() {
        val items = if (isMonthly) calculateMonthly() else calculateYearly()
        val header = if (isMonthly) "Month,Principal,Interest,Balance\n" else "Year,Principal,Interest,Balance\n"
        val csvContent = StringBuilder(header)
        
        for (item in items) {
            csvContent.append("${item.label},${item.principal.replace(",", "")},${item.interest.replace(",", "")},${item.balance.replace(",", "")}\n")
        }

        try {
            val cachePath = File(cacheDir, "csv")
            cachePath.mkdirs()
            val csvFile = File(cachePath, "Calculation_Stats.csv")
            val stream = FileOutputStream(csvFile)
            stream.write(csvContent.toString().toByteArray())
            stream.close()

            val contentUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", csvFile)

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, "text/csv")
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "Detailed calculation stats from SCI-CAL app. \nDownload now: https://play.google.com/store/apps/details?id=com.tahir.scical")
                }
                startActivity(Intent.createChooser(shareIntent, "Export Stats as CSV"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateMonthly(): List<StatItem> {
        val list = mutableListOf<StatItem>()
        val df = DecimalFormat("#,###.##")
        var balance = if (isRecurring) 0.0 else principal
        val monthlyRate = (annualRate / 100) / 12

        if (isInvestment) {
            for (i in 1..totalMonths) {
                if (isRecurring) balance += principal
                val interest = balance * monthlyRate
                balance += interest
                list.add(StatItem("Month $i", if(isRecurring) df.format(principal) else "-", df.format(interest), df.format(balance)))
            }
        } else {
            val emi = if (monthlyRate > 0) {
                (principal * monthlyRate * (1 + monthlyRate).pow(totalMonths)) / ((1 + monthlyRate).pow(totalMonths) - 1)
            } else principal / totalMonths

            var remainingBalance = principal
            for (i in 1..totalMonths) {
                val interest = remainingBalance * monthlyRate
                val principalPart = emi - interest
                remainingBalance -= principalPart
                list.add(StatItem("Month $i", df.format(principalPart), df.format(interest), df.format(maxOf(0.0, remainingBalance))))
            }
        }
        return list
    }

    private fun calculateYearly(): List<StatItem> {
        val list = mutableListOf<StatItem>()
        val df = DecimalFormat("#,###.##")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        val monthlyItems = calculateMonthly()
        var yearPrincipal = 0.0
        var yearInterest = 0.0
        
        for (i in monthlyItems.indices) {
            val item = monthlyItems[i]
            if (item.principal != "-") {
                yearPrincipal += item.principal.replace(",", "").toDoubleOrNull() ?: 0.0
            }
            yearInterest += item.interest.replace(",", "").toDoubleOrNull() ?: 0.0
            
            if ((i + 1) % 12 == 0 || i == monthlyItems.size - 1) {
                val yearNum = (i / 12) + 1
                list.add(StatItem("Year ${currentYear + yearNum - 1}", if (isInvestment && !isRecurring) "-" else df.format(yearPrincipal), df.format(yearInterest), item.balance))
                yearPrincipal = 0.0
                yearInterest = 0.0
            }
        }
        return list
    }

    data class StatItem(val label: String, val principal: String, val interest: String, val balance: String)

    inner class StatsAdapter(private val items: List<StatItem>) : RecyclerView.Adapter<StatsAdapter.ViewHolder>() {
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvLabel: TextView = v.findViewById(R.id.tvLabel)
            val tvPrincipal: TextView = v.findViewById(R.id.tvPrincipal)
            val tvInterest: TextView = v.findViewById(R.id.tvInterest)
            val tvBalance: TextView = v.findViewById(R.id.tvBalance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_stat_row, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(h: ViewHolder, p: Int) {
            val item = items[p]
            h.tvLabel.text = item.label
            h.tvPrincipal.text = item.principal
            h.tvInterest.text = item.interest
            h.tvBalance.text = item.balance
        }

        override fun getItemCount() = items.size
    }
}
