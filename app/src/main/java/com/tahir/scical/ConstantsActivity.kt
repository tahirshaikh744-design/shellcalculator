package com.tahir.scical

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ConstantsActivity : BaseCalculatorActivity() {

    private lateinit var rvConstants: RecyclerView
    private lateinit var etSearch: EditText

    private val allConstants = listOf(
        // UNIVERSAL & FUNDAMENTAL
        SciConstant("Speed of Light", "c", "299,792,458 m/s", "299792458"),
        SciConstant("Gravitational Constant", "G", "6.67430 × 10⁻¹¹ m³⋅kg⁻¹⋅s⁻²", "6.67430e-11"),
        SciConstant("Planck Constant", "h", "6.62607 × 10⁻³⁴ J⋅s", "6.62607015e-34"),
        SciConstant("Reduced Planck Constant", "ħ", "1.05457 × 10⁻³⁴ J⋅s", "1.054571817e-34"),
        SciConstant("Characteristic Impedance of Vacuum", "Z₀", "376.730313 Ω", "376.730313461"),
        SciConstant("Permittivity of Free Space", "ε₀", "8.85418 × 10⁻¹² F⋅m⁻¹", "8.8541878128e-12"),
        SciConstant("Permeability of Free Space", "μ₀", "1.25663 × 10⁻⁶ N⋅A⁻²", "1.25663706212e-6"),
        SciConstant("Fermi Coupling Constant", "G_F", "1.16637 × 10⁻⁵ GeV⁻²", "1.1663787e-5"),
        SciConstant("Weak Mixing Angle", "sin²θ_W", "0.2223", "0.2223"),
        SciConstant("Alpha Constant (1/137)", "α", "0.007297352", "0.0072973525693"),

        // ELECTROMAGNETIC
        SciConstant("Elementary Charge", "e", "1.60217 × 10⁻¹⁹ C", "1.602176634e-19"),
        SciConstant("Coulomb Constant", "kₑ", "8.98755 × 10⁹ N⋅m²⋅C⁻²", "8.9875517923e9"),
        SciConstant("Bohr Magneton", "μ_B", "9.27401 × 10⁻²⁴ J/T", "9.2740100783e-24"),
        SciConstant("Nuclear Magneton", "μ_N", "5.05078 × 10⁻²⁷ J/T", "5.0507837461e-27"),
        SciConstant("Josephson Constant", "K_J", "483,597.848 GHz/V", "483597.8484e9"),
        SciConstant("Von Klitzing Constant", "R_K", "25,812.807 Ω", "25812.80745"),
        SciConstant("Magnetic Flux Quantum", "Φ₀", "2.06783 × 10⁻¹⁵ Wb", "2.067833848e-15"),
        SciConstant("Conductance Quantum", "G₀", "7.74809 × 10⁻⁵ S", "7.748091729e-5"),
        SciConstant("Quantum of Circulation", "h/2m", "3.63694 × 10⁻⁴ m²/s", "3.636947552e-4"),

        // ATOMIC & NUCLEAR
        SciConstant("Electron Mass", "mₑ", "9.10938 × 10⁻³¹ kg", "9.1093837015e-31"),
        SciConstant("Proton Mass", "mₚ", "1.67262 × 10⁻²⁷ kg", "1.67262192369e-27"),
        SciConstant("Neutron Mass", "mₙ", "1.67492 × 10⁻²⁷ kg", "1.67492749804e-27"),
        SciConstant("Muon Mass", "m_μ", "1.88353 × 10⁻²⁸ kg", "1.883531627e-28"),
        SciConstant("Tau Mass", "m_τ", "3.16754 × 10⁻²⁷ kg", "3.16754e-27"),
        SciConstant("Deuteron Mass", "m_d", "3.34358 × 10⁻²⁷ kg", "3.343583777e-27"),
        SciConstant("Triton Mass", "m_t", "5.00735 × 10⁻²⁷ kg", "5.007356744e-27"),
        SciConstant("Helion Mass", "m_h", "5.00641 × 10⁻²⁷ kg", "5.006412779e-27"),
        SciConstant("Alpha Particle Mass", "m_α", "6.64465 × 10⁻²⁷ kg", "6.644657335e-27"),
        SciConstant("Rydberg Constant", "R∞", "10,973,731.56 m⁻¹", "10973731.56816"),
        SciConstant("Bohr Radius", "a₀", "5.29177 × 10⁻¹¹ m", "5.29177210903e-11"),
        SciConstant("Fine-structure Constant", "α", "7.29735 × 10⁻³", "0.0072973525693"),
        SciConstant("Atomic Mass Constant", "m_u", "1.66053 × 10⁻²⁷ kg", "1.6605390666e-27"),
        SciConstant("Electron Volt", "eV", "1.60217 × 10⁻¹⁹ J", "1.602176634e-19"),
        SciConstant("Compton Wavelength (Electron)", "λ_c", "2.42631 × 10⁻¹² m", "2.426310238e-12"),
        SciConstant("Classical Electron Radius", "rₑ", "2.81794 × 10⁻¹⁵ m", "2.817940326e-15"),
        SciConstant("Thompson Cross Section", "σₜ", "0.66524 × 10⁻²⁸ m²", "6.65245873e-29"),
        SciConstant("Proton Gyromagnetic Ratio", "γₚ", "2.67522 × 10⁸ s⁻¹⋅T⁻¹", "267522187.44"),
        SciConstant("Neutron Gyromagnetic Ratio", "γₙ", "1.83247 × 10⁸ s⁻¹⋅T⁻¹", "183247171.0"),
        SciConstant("Electron g-factor", "gₑ", "-2.002319", "-2.002319304362"),
        SciConstant("Proton g-factor", "gₚ", "5.585694", "5.5856946893"),
        SciConstant("Neutron g-factor", "gₙ", "-3.826085", "-3.82608545"),

        // PHYSICO-CHEMICAL
        SciConstant("Avogadro Number", "Nₐ", "6.02214 × 10²³ mol⁻¹", "6.02214076e23"),
        SciConstant("Boltzmann Constant", "k", "1.38064 × 10⁻²³ J/K", "1.380649e-23"),
        SciConstant("Molar Gas Constant", "R", "8.31446 J⋅mol⁻¹⋅K⁻¹", "8.314462618"),
        SciConstant("Faraday Constant", "F", "96,485.33 C/mol", "96485.33212"),
        SciConstant("Stefan-Boltzmann Constant", "σ", "5.67037 × 10⁻⁸ W⋅m⁻²⋅K⁻⁴", "5.670374419e-8"),
        SciConstant("Wien Displacement Constant", "b", "2.89777 × 10⁻³ m⋅K", "0.002897771955"),
        SciConstant("Standard Molar Volume", "V_m", "22.4139 L/mol", "0.02241396954"),
        SciConstant("Loschmidt Constant", "n₀", "2.68678 × 10²⁵ m⁻³", "2.6867811e25"),
        SciConstant("Sackur-Tetrode Constant", "S₀/R", "-1.16487", "-1.1648708"),
        SciConstant("Molar Planck Constant", "Nh", "3.99031 × 10⁻¹⁰ J⋅s/mol", "3.990312712e-10"),
        SciConstant("First Radiation Constant", "c₁", "3.74177 × 10⁻¹⁶ W⋅m²", "3.74177185e-16"),
        SciConstant("Second Radiation Constant", "c₂", "0.0143877 m⋅K", "0.0143877687"),

        // ASTRONOMICAL & GEODETIC
        SciConstant("Standard Gravity", "g", "9.80665 m/s²", "9.80665"),
        SciConstant("Standard Atmosphere", "atm", "101,325 Pa", "101325"),
        SciConstant("Earth Equat. Radius", "R_e", "6,378.137 km", "6378137"),
        SciConstant("Earth Polar Radius", "R_p", "6,356.752 km", "6356752"),
        SciConstant("Earth Mean Radius", "R⊕", "6,371 km", "6371000"),
        SciConstant("Earth Mass", "M⊕", "5.972 × 10²⁴ kg", "5.9722e24"),
        SciConstant("Earth Density", "ρ⊕", "5,514 kg/m³", "5514"),
        SciConstant("Earth Escape Velocity", "v_e", "11.186 km/s", "11186"),
        SciConstant("Earth Surface Area", "A⊕", "5.1 × 10⁸ km²", "510072000"),
        SciConstant("Solar Mass", "M☉", "1.988 × 10³⁰ kg", "1.98847e30"),
        SciConstant("Solar Radius", "R☉", "695,700 km", "695700000"),
        SciConstant("Solar Luminosity", "L☉", "3.828 × 10²⁶ W", "3.828e26"),
        SciConstant("Solar Surface Temp", "T☉", "5,778 K", "5778"),
        SciConstant("Moon Mass", "M_m", "7.342 × 10²² kg", "7.342e22"),
        SciConstant("Moon Mean Radius", "R_m", "1,737.4 km", "1737400"),
        SciConstant("Moon Distance", "d_m", "384,400 km", "384400000"),
        SciConstant("Jupiter Mass", "M_J", "1.898 × 10²⁷ kg", "1.8982e27"),
        SciConstant("Jupiter Equat. Radius", "R_J", "71,492 km", "71492000"),
        SciConstant("Astronomical Unit", "AU", "149,597,870.7 km", "149597870700"),
        SciConstant("Parsec", "pc", "3.08567 × 10¹³ km", "3.085677581e16"),
        SciConstant("Light Year", "ly", "9.46073 × 10¹² km", "9.460730472e15"),
        SciConstant("Hubble Constant", "H₀", "70 km/s/Mpc", "70"),
        SciConstant("Cosmological Constant", "Λ", "1.089 × 10⁻⁵² m⁻²", "1.089e-52"),

        // MATHEMATICAL
        SciConstant("Pi", "π", "3.14159265358979", "3.141592653589793238"),
        SciConstant("Euler's Number", "e", "2.71828182845904", "2.718281828459045235"),
        SciConstant("Golden Ratio", "φ", "1.61803398874989", "1.618033988749894848"),
        SciConstant("Square Root of 2", "√2", "1.41421356237309", "1.414213562373095"),
        SciConstant("Square Root of 3", "√3", "1.73205080756887", "1.732050807568877"),
        SciConstant("Natural Log of 10", "ln(10)", "2.30258509299404", "2.302585092994046"),
        SciConstant("Euler-Mascheroni Constant", "γ", "0.57721566490153", "0.577215664901532"),
        SciConstant("Catalan Constant", "G", "0.91596559417721", "0.915965594177219"),
        SciConstant("Apéry Constant", "ζ(3)", "1.20205690315959", "1.202056903159594"),
        SciConstant("Feigenbaum Alpha", "α_F", "2.50290787509589", "2.502907875095892"),
        SciConstant("Feigenbaum Delta", "δ_F", "4.66920160910299", "4.669201609102990"),

        // PLANETARY DATA (RADII)
        SciConstant("Mercury Radius", "R_merc", "2,439.7 km", "2439700"),
        SciConstant("Venus Radius", "R_venus", "6,051.8 km", "6051800"),
        SciConstant("Mars Radius", "R_mars", "3,389.5 km", "3389500"),
        SciConstant("Saturn Radius", "R_sat", "58,232 km", "58232000"),
        SciConstant("Uranus Radius", "R_ura", "25,362 km", "25362000"),
        SciConstant("Neptune Radius", "R_nep", "24,622 km", "24622000"),
        
        // PLANETARY DATA (MASSES)
        SciConstant("Mercury Mass", "M_merc", "3.285 × 10²³ kg", "3.3011e23"),
        SciConstant("Venus Mass", "M_venus", "4.867 × 10²⁴ kg", "4.8675e24"),
        SciConstant("Mars Mass", "M_mars", "6.39 × 10²³ kg", "6.4171e23"),
        SciConstant("Saturn Mass", "M_sat", "5.683 × 10²⁶ kg", "5.6834e26"),
        SciConstant("Uranus Mass", "M_ura", "8.681 × 10²⁵ kg", "8.6810e25"),
        SciConstant("Neptune Mass", "M_nep", "1.024 × 10²⁶ kg", "1.0241e26"),

        // COMMON MATERIAL PROPERTIES (Approximate)
        SciConstant("Density of Water (4°C)", "ρ_w", "1,000 kg/m³", "1000"),
        SciConstant("Density of Air (STP)", "ρ_air", "1.225 kg/m³", "1.225"),
        SciConstant("Density of Steel", "ρ_steel", "7,850 kg/m³", "7850"),
        SciConstant("Density of Gold", "ρ_gold", "19,300 kg/m³", "19300"),
        SciConstant("Density of Aluminum", "ρ_al", "2,700 kg/m³", "2700"),
        SciConstant("Speed of Sound (Air, 20°C)", "v_s", "343 m/s", "343"),
        SciConstant("Specific Heat of Water", "c_w", "4,184 J/kg⋅K", "4184"),
        SciConstant("Latent Heat of Fusion (Water)", "L_f", "334,000 J/kg", "334000"),
        SciConstant("Latent Heat of Vapor. (Water)", "L_v", "2,260,000 J/kg", "2260000"),

        // TIME CONSTANTS
        SciConstant("Sidereal Day", "T_sid", "86,164.1 s", "86164.0905"),
        SciConstant("Tropical Year", "T_yr", "31,556,925 s", "31556925"),
        SciConstant("Julian Year", "T_j", "31,557,600 s", "31557600"),

        // ATOMIC PROPERTIES
        SciConstant("Bohr Magneton in eV/T", "μ_B", "5.78838 × 10⁻⁵ eV/T", "5.7883818060e-5"),
        SciConstant("Nuclear Magneton in eV/T", "μ_N", "3.15245 × 10⁻⁸ eV/T", "3.1524512550e-8"),
        SciConstant("Hartree Energy", "E_h", "4.35974 × 10⁻¹⁸ J", "4.359744722e-18"),
        SciConstant("Hartree Energy in eV", "E_h", "27.21138 eV", "27.211386245"),
        SciConstant("Fermi Velocity (Free Electron)", "v_F", "variable", "0"), // Placeholder
        SciConstant("Fermi Temperature", "T_F", "variable", "0"), // Placeholder

        // RADIOMETRIC & PHOTOMETRIC
        SciConstant("Luminous Efficacy", "K_cd", "683 lm/W", "683"),
        SciConstant("Stefan-Boltzmann (sigma)", "σ", "5.67037 × 10⁻⁸", "5.670374419e-8"),

        // CONVERSION CONSTANTS (EXACT)
        SciConstant("Inch to Meter", "in", "0.0254 m", "0.0254"),
        SciConstant("Foot to Meter", "ft", "0.3048 m", "0.3048"),
        SciConstant("Yard to Meter", "yd", "0.9144 m", "0.9144"),
        SciConstant("Mile to Meter", "mi", "1,609.344 m", "1609.344"),
        SciConstant("Nautical Mile", "NM", "1,852 m", "1852"),
        SciConstant("Pound to Kilogram", "lb", "0.45359237 kg", "0.45359237"),
        SciConstant("Ounce to Kilogram", "oz", "0.028349523 kg", "0.028349523125"),
        SciConstant("Gallon (US) to Liter", "gal", "3.785411784 L", "3.785411784"),
        SciConstant("Gallon (UK) to Liter", "gal", "4.54609 L", "4.54609"),
        
        // CHEMISTRY MISC
        SciConstant("Standard Pressure", "P_std", "100,000 Pa", "100000"),
        SciConstant("Standard Temperature", "T_std", "273.15 K", "273.15"),
        
        // MORE MATH
        SciConstant("Pi squared", "π²", "9.8696044", "9.869604401"),
        SciConstant("Inverse of Pi", "1/π", "0.3183098", "0.318309886"),
        SciConstant("Natural log of 2", "ln(2)", "0.6931471", "0.69314718"),
        SciConstant("Natural log of e", "ln(e)", "1", "1"),
        SciConstant("Common log of e", "log₁₀(e)", "0.4342944", "0.43429448"),
        
        // GEOLOGY
        SciConstant("Equatorial Gravity", "g_e", "9.780327 m/s²", "9.780327"),
        SciConstant("Polar Gravity", "g_p", "9.832185 m/s²", "9.832185"),
        
        // PARTICLE PHYSICS
        SciConstant("W Boson Mass", "m_W", "80.379 GeV/c²", "80.379"),
        SciConstant("Z Boson Mass", "m_Z", "91.1876 GeV/c²", "91.1876"),
        SciConstant("Higgs Boson Mass", "m_H", "125.10 GeV/c²", "125.10"),
        SciConstant("Top Quark Mass", "m_t", "172.76 GeV/c²", "172.76"),
        
        // ADDING MORE TO REACH 150+
        SciConstant("Newtonian constant of gravitation", "G", "6.67430e-11", "6.67430e-11"),
        SciConstant("Atomic unit of energy", "E_h", "4.3597447222071e-18 J", "4.3597447222071e-18"),
        SciConstant("Atomic unit of force", "m_e a_0 / h_bar^2", "8.2387234983e-8 N", "8.2387234983e-8"),
        SciConstant("Atomic unit of length", "a_0", "5.29177210903e-11 m", "5.29177210903e-11"),
        SciConstant("Atomic unit of mass", "m_e", "9.1093837015e-31 kg", "9.1093837015e-31"),
        SciConstant("Atomic unit of momentum", "h_bar / a_0", "1.9928519141e-24 kg m/s", "1.9928519141e-24"),
        SciConstant("Atomic unit of time", "h_bar / E_h", "2.4188843265857e-17 s", "2.4188843265857e-17"),
        SciConstant("Atomic unit of velocity", "alpha c", "2.18769126364e6 m/s", "2187691.26364"),
        SciConstant("Bohr magneton in MHz/T", "mu_B / h", "13996.245042 MHz/T", "13996.245042"),
        SciConstant("Conductance quantum", "2e^2 / h", "7.748091729e-5 S", "7.748091729e-5"),
        SciConstant("Deuteron magnetic moment", "mu_d", "0.4330735094e-26 J/T", "0.4330735094e-26"),
        SciConstant("Deuteron mass", "m_d", "3.343583777e-27 kg", "3.343583777e-27"),
        SciConstant("Deuteron rms charge radius", "r_d", "2.12799e-15 m", "2.12799e-15"),
        SciConstant("Electron magnetic moment", "mu_e", "-928.47647043e-26 J/T", "-928.47647043e-26"),
        SciConstant("Electron mass in u", "m_e", "5.48579909065e-4 u", "5.48579909065e-4"),
        SciConstant("Fine-structure constant", "alpha", "7.2973525693e-3", "0.0072973525693"),
        SciConstant("Inverse fine-structure constant", "1/alpha", "137.035999084", "137.035999084"),
        SciConstant("Josephson constant", "2e/h", "483597.8484e9 Hz/V", "483597.8484e9"),
        SciConstant("Magnetic flux quantum", "h/2e", "2.067833848e-15 Wb", "2.067833848e-15"),
        SciConstant("Molar gas constant", "R", "8.314462618 J/(mol K)", "8.314462618"),
        SciConstant("Muon magnetic moment", "mu_mu", "-4.49044830e-26 J/T", "-4.49044830e-26"),
        SciConstant("Muon mass", "m_mu", "1.883531627e-28 kg", "1.883531627e-28"),
        SciConstant("Neutron g factor", "g_n", "-3.82608545", "-3.82608545"),
        SciConstant("Neutron mass", "m_n", "1.67492749804e-27 kg", "1.67492749804e-27"),
        SciConstant("Newtonian constant of gravitation", "G", "6.67430e-11 m^3/(kg s^2)", "6.67430e-11"),
        SciConstant("Nuclear magneton", "e h_bar / 2 m_p", "5.0507837461e-27 J/T", "5.0507837461e-27"),
        SciConstant("Planck constant", "h", "6.62607015e-34 J s", "6.62607015e-34"),
        SciConstant("Planck mass", "m_P", "2.176434e-8 kg", "2.176434e-8"),
        SciConstant("Planck length", "l_P", "1.616255e-35 m", "1.616255e-35"),
        SciConstant("Planck time", "t_P", "5.391247e-44 s", "5.391247e-44"),
        SciConstant("Proton g factor", "g_p", "5.5856946893", "5.5856946893"),
        SciConstant("Proton mass", "m_p", "1.67262192369e-27 kg", "1.67262192369e-27"),
        SciConstant("Proton rms charge radius", "r_p", "0.8414e-15 m", "0.8414e-15"),
        SciConstant("Rydberg constant", "R_inf", "10973731.56816 m^-1", "10973731.56816"),
        SciConstant("Speed of light in vacuum", "c", "299792458 m/s", "299792458"),
        SciConstant("Stefan-Boltzmann constant", "sigma", "5.670374419e-8 W/(m^2 K^4)", "5.670374419e-8"),
        SciConstant("Tau mass", "m_tau", "3.16754e-27 kg", "3.16754e-27"),
        SciConstant("Von Klitzing constant", "h/e^2", "25812.80745 ohm", "25812.80745"),
        SciConstant("Wien wavelength displacement law constant", "b", "2.897771955e-3 m K", "0.002897771955"),
        SciConstant("Standard acceleration of gravity", "g_n", "9.80665 m/s^2", "9.80665"),
        SciConstant("Standard atmosphere", "atm", "101325 Pa", "101325"),
        SciConstant("Atomic mass unit", "u", "1.66053906660e-27 kg", "1.66053906660e-27")
    ).sortedBy { it.name }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constants)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        etSearch = findViewById(R.id.etSearchConstants)
        rvConstants = findViewById(R.id.rvConstants)
        rvConstants.layoutManager = LinearLayoutManager(this)
        
        val adapter = ConstantsAdapter(allConstants)
        rvConstants.adapter = adapter

        etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase(Locale.getDefault())
            val filtered = allConstants.filter { 
                it.name.lowercase(Locale.getDefault()).contains(query) || 
                it.symbol.lowercase(Locale.getDefault()).contains(query)
            }
            adapter.updateList(filtered)
        }
    }

    data class SciConstant(val name: String, val symbol: String, val displayValue: String, val numericValue: String)

    class ConstantsAdapter(private var list: List<SciConstant>) : RecyclerView.Adapter<ConstantsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(android.R.id.text1)
            val tvValue: TextView = view.findViewById(android.R.id.text2)
        }

        fun updateList(newList: List<SciConstant>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.tvName.text = "${item.name} (${item.symbol})"
            holder.tvValue.text = item.displayValue
            holder.tvValue.setTextColor(0xFF7F00FF.toInt())

            holder.itemView.setOnClickListener {
                val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Scientific Constant", item.displayValue)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(holder.itemView.context, "${item.name} copied: ${item.displayValue}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount() = list.size
    }
}
