package com.tahir.scical

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SmartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_smart_features, container, false)
        
        setupClickListeners(view)
        
        return view
    }

    private fun setupClickListeners(view: View) {
        val smartTools = mapOf(
            R.id.btnUnitWorldClock to WorldClockActivity::class.java,
            R.id.btnUnitConstants to ConstantsActivity::class.java,
            R.id.btnUnitCapitals to CapitalsActivity::class.java
        )

        for ((id, activityClass) in smartTools) {
            view.findViewById<View>(id)?.setOnClickListener {
                (activity as? MainActivity)?.vibrate()
                startActivity(Intent(requireContext(), activityClass))
            }
        }
    }
}
