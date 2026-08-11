package com.tahir.scical

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import java.util.Locale

class ConverterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_unit_converter, container, false)
        
        setupSearch(view)
        setupClickListeners(view)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Programmatically enforce theme-aware background color
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_main))
    }

    private fun setupSearch(view: View) {
        val etSearch = view.findViewById<EditText>(R.id.etSearchConverter)
        val grid = view.findViewById<GridLayout>(R.id.converterGrid)
        
        etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase(Locale.getDefault())
            for (i in 0 until grid.childCount) {
                val child = grid.getChildAt(i)
                val tag = child.tag?.toString()?.lowercase(Locale.getDefault()) ?: ""
                val textView = (child as? LinearLayout)?.getChildAt(1) as? TextView
                val label = textView?.text?.toString()?.lowercase(Locale.getDefault()) ?: ""
                
                if (label.contains(query) || tag.contains(query)) {
                    child.visibility = View.VISIBLE
                } else {
                    child.visibility = View.GONE
                }
            }
        }
    }

    private fun setupClickListeners(view: View) {
        val converters: Map<Int, Class<*>> = mapOf(
            R.id.btnUnitAge to AgeCalculatorActivity::class.java,
            R.id.btnUnitArea to AreaCalculatorActivity::class.java,
            R.id.btnUnitBmi to BmiCalculatorActivity::class.java,
            R.id.btnUnitData to DataCalculatorActivity::class.java,
            R.id.btnUnitDate to DateCalculatorActivity::class.java,
            R.id.btnUnitDiscount to DiscountCalculatorActivity::class.java,
            R.id.btnUnitLength to LengthCalculatorActivity::class.java,
            R.id.btnUnitMass to MassCalculatorActivity::class.java,
            R.id.btnUnitNumeral to NumeralSystemActivity::class.java,
            R.id.btnUnitSpeed to SpeedCalculatorActivity::class.java,
            R.id.btnUnitTemperature to TemperatureCalculatorActivity::class.java,
            R.id.btnUnitTime to TimeCalculatorActivity::class.java,
            R.id.btnUnitVolume to VolumeCalculatorActivity::class.java,
            R.id.btnUnitAngle to AngleCalculatorActivity::class.java,
            R.id.btnUnitEnergy to EnergyCalculatorActivity::class.java,
            R.id.btnUnitForce to ForceCalculatorActivity::class.java,
            R.id.btnUnitFuel to FuelEfficiencyCalculatorActivity::class.java,
            R.id.btnUnitPower to PowerCalculatorActivity::class.java,
            R.id.btnUnitPressure to PressureCalculatorActivity::class.java,
            R.id.btnUnitPercentage to PercentageCalculatorActivity::class.java,
            R.id.btnUnitPrice to UnitPriceCalculatorActivity::class.java,
            R.id.btnUnitTip to TipCalculatorActivity::class.java,
            // Batch 1
            R.id.btnUnitTorque to TorqueCalculatorActivity::class.java,
            R.id.btnUnitFrequency to FrequencyCalculatorActivity::class.java,
            R.id.btnUnitVolumeFlow to VolumeFlowCalculatorActivity::class.java,
            R.id.btnUnitCooking to CookingCalculatorActivity::class.java,
            R.id.btnUnitTransferRate to TransferRateCalculatorActivity::class.java,
            // Batch 2
            R.id.btnUnitAcceleration to AccelerationCalculatorActivity::class.java,
            R.id.btnUnitSound to SoundCalculatorActivity::class.java,
            R.id.btnUnitCurrent to ElectricCurrentCalculatorActivity::class.java,
            R.id.btnUnitVoltage to VoltageCalculatorActivity::class.java,
            R.id.btnUnitResistance to ResistanceCalculatorActivity::class.java,
            // Batch 3
            R.id.btnUnitDensity to DensityCalculatorActivity::class.java,
            R.id.btnUnitIlluminance to IlluminanceCalculatorActivity::class.java,
            R.id.btnUnitRadiation to RadiationCalculatorActivity::class.java,
            R.id.btnUnitCharge to ElectricChargeCalculatorActivity::class.java,
            R.id.btnUnitInductance to InductanceCalculatorActivity::class.java
        )

        for ((id, activityClass) in converters) {
            view.findViewById<View>(id)?.setOnClickListener {
                (activity as? MainActivity)?.vibrate()
                startActivity(Intent(requireContext(), activityClass))
            }
        }
    }
}
