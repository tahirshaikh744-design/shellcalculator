package com.tahir.scical

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FinanceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finance_converter, container, false)
        
        setupClickListeners(view)
        
        return view
    }

    private fun setupClickListeners(view: View) {
        val financeTools: Map<Int, Class<*>> = mapOf(
            R.id.btnUnitGst to GstCalculatorActivity::class.java,
            R.id.btnUnitCurrency to CurrencyCalculatorActivity::class.java,
            R.id.btnUnitInvestment to InvestmentCalculatorActivity::class.java,
            R.id.btnUnitLoan to LoanCalculatorActivity::class.java,
            R.id.btnUnitInflation to InflationCalculatorActivity::class.java,
            R.id.btnUnitFD to FdCalculatorActivity::class.java,
            R.id.btnUnitRD to RdCalculatorActivity::class.java,
            R.id.btnUnitTax to TaxCalculatorActivity::class.java
        )

        for ((id, activityClass) in financeTools) {
            view.findViewById<View>(id)?.setOnClickListener {
                (activity as? MainActivity)?.vibrate()
                startActivity(Intent(requireContext(), activityClass))
            }
        }
    }
}
