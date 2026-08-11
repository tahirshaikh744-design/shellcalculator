package com.tahir.scical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CapitalsActivity : BaseCalculatorActivity() {

    private lateinit var rvCapitals: RecyclerView
    private lateinit var etSearch: EditText
    private val allCapitals = listOf(
        CountryCapital("Afghanistan", "Kabul", "AF"),
        CountryCapital("Albania", "Tirana", "AL"),
        CountryCapital("Algeria", "Algiers", "DZ"),
        CountryCapital("Andorra", "Andorra la Vella", "AD"),
        CountryCapital("Angola", "Luanda", "AO"),
        CountryCapital("Antigua and Barbuda", "Saint John's", "AG"),
        CountryCapital("Argentina", "Buenos Aires", "AR"),
        CountryCapital("Armenia", "Yerevan", "AM"),
        CountryCapital("Australia", "Canberra", "AU"),
        CountryCapital("Austria", "Vienna", "AT"),
        CountryCapital("Azerbaijan", "Baku", "AZ"),
        CountryCapital("Bahamas", "Nassau", "BS"),
        CountryCapital("Bahrain", "Manama", "BH"),
        CountryCapital("Bangladesh", "Dhaka", "BD"),
        CountryCapital("Barbados", "Bridgetown", "BB"),
        CountryCapital("Belarus", "Minsk", "BY"),
        CountryCapital("Belgium", "Brussels", "BE"),
        CountryCapital("Belize", "Belmopan", "BZ"),
        CountryCapital("Benin", "Porto-Novo", "BJ"),
        CountryCapital("Bhutan", "Thimphu", "BT"),
        CountryCapital("Bolivia", "Sucre", "BO"),
        CountryCapital("Bosnia and Herzegovina", "Sarajevo", "BA"),
        CountryCapital("Botswana", "Gaborone", "BW"),
        CountryCapital("Brazil", "Brasilia", "BR"),
        CountryCapital("Brunei", "Bandar Seri Begawan", "BN"),
        CountryCapital("Bulgaria", "Sofia", "BG"),
        CountryCapital("Burkina Faso", "Ouagadougou", "BF"),
        CountryCapital("Burundi", "Gitega", "BI"),
        CountryCapital("Cambodia", "Phnom Penh", "KH"),
        CountryCapital("Cameroon", "Yaounde", "CM"),
        CountryCapital("Canada", "Ottawa", "CA"),
        CountryCapital("Cape Verde", "Praia", "CV"),
        CountryCapital("Central African Republic", "Bangui", "CF"),
        CountryCapital("Chad", "N'Djamena", "TD"),
        CountryCapital("Chile", "Santiago", "CL"),
        CountryCapital("China", "Beijing", "CN"),
        CountryCapital("Colombia", "Bogota", "CO"),
        CountryCapital("Comoros", "Moroni", "KM"),
        CountryCapital("Congo", "Brazzaville", "CG"),
        CountryCapital("Costa Rica", "San Jose", "CR"),
        CountryCapital("Croatia", "Zagreb", "HR"),
        CountryCapital("Cuba", "Havana", "CU"),
        CountryCapital("Cyprus", "Nicosia", "CY"),
        CountryCapital("Czech Republic", "Prague", "CZ"),
        CountryCapital("Denmark", "Copenhagen", "DK"),
        CountryCapital("Djibouti", "Djibouti", "DJ"),
        CountryCapital("Dominica", "Roseau", "DM"),
        CountryCapital("Dominican Republic", "Santo Domingo", "DO"),
        CountryCapital("Ecuador", "Quito", "EC"),
        CountryCapital("Egypt", "Cairo", "EG"),
        CountryCapital("El Salvador", "San Salvador", "SV"),
        CountryCapital("Equatorial Guinea", "Malabo", "GQ"),
        CountryCapital("Eritrea", "Asmara", "ER"),
        CountryCapital("Estonia", "Tallinn", "EE"),
        CountryCapital("Eswatini", "Mbabane", "SZ"),
        CountryCapital("Ethiopia", "Addis Ababa", "ET"),
        CountryCapital("Fiji", "Suva", "FJ"),
        CountryCapital("Finland", "Helsinki", "FI"),
        CountryCapital("France", "Paris", "FR"),
        CountryCapital("Gabon", "Libreville", "GA"),
        CountryCapital("Gambia", "Banjul", "GM"),
        CountryCapital("Georgia", "Tbilisi", "GE"),
        CountryCapital("Germany", "Berlin", "DE"),
        CountryCapital("Ghana", "Accra", "GH"),
        CountryCapital("Greece", "Athens", "GR"),
        CountryCapital("Grenada", "Saint George's", "GD"),
        CountryCapital("Guatemala", "Guatemala City", "GT"),
        CountryCapital("Guinea", "Conakry", "GN"),
        CountryCapital("Guinea-Bissau", "Bissau", "GW"),
        CountryCapital("Guyana", "Georgetown", "GY"),
        CountryCapital("Haiti", "Port-au-Prince", "HT"),
        CountryCapital("Honduras", "Tegucigalpa", "HN"),
        CountryCapital("Hungary", "Budapest", "HU"),
        CountryCapital("Iceland", "Reykjavik", "IS"),
        CountryCapital("India", "New Delhi", "IN"),
        CountryCapital("Indonesia", "Jakarta", "ID"),
        CountryCapital("Iran", "Tehran", "IR"),
        CountryCapital("Iraq", "Baghdad", "IQ"),
        CountryCapital("Ireland", "Dublin", "IE"),
        CountryCapital("Israel", "Jerusalem", "IL"),
        CountryCapital("Italy", "Rome", "IT"),
        CountryCapital("Jamaica", "Kingston", "JM"),
        CountryCapital("Japan", "Tokyo", "JP"),
        CountryCapital("Jordan", "Amman", "JO"),
        CountryCapital("Kazakhstan", "Astana", "KZ"),
        CountryCapital("Kenya", "Nairobi", "KE"),
        CountryCapital("Kiribati", "South Tarawa", "KI"),
        CountryCapital("Kuwait", "Kuwait City", "KW"),
        CountryCapital("Kyrgyzstan", "Bishkek", "KG"),
        CountryCapital("Laos", "Vientiane", "LA"),
        CountryCapital("Latvia", "Riga", "LV"),
        CountryCapital("Lebanon", "Beirut", "LB"),
        CountryCapital("Lesotho", "Maseru", "LS"),
        CountryCapital("Liberia", "Monrovia", "LR"),
        CountryCapital("Libya", "Tripoli", "LY"),
        CountryCapital("Liechtenstein", "Vaduz", "LI"),
        CountryCapital("Lithuania", "Vilnius", "LT"),
        CountryCapital("Luxembourg", "Luxembourg", "LU"),
        CountryCapital("Madagascar", "Antananarivo", "MG"),
        CountryCapital("Malawi", "Lilongwe", "MW"),
        CountryCapital("Malaysia", "Kuala Lumpur", "MY"),
        CountryCapital("Maldives", "Male", "MV"),
        CountryCapital("Mali", "Bamako", "ML"),
        CountryCapital("Malta", "Valletta", "MT"),
        CountryCapital("Marshall Islands", "Majuro", "MH"),
        CountryCapital("Mauritania", "Nouakchott", "MR"),
        CountryCapital("Mauritius", "Port Louis", "MU"),
        CountryCapital("Mexico", "Mexico City", "MX"),
        CountryCapital("Micronesia", "Palikir", "FM"),
        CountryCapital("Moldova", "Chisinau", "MD"),
        CountryCapital("Monaco", "Monaco", "MC"),
        CountryCapital("Mongolia", "Ulaanbaatar", "MN"),
        CountryCapital("Montenegro", "Podgorica", "ME"),
        CountryCapital("Morocco", "Rabat", "MA"),
        CountryCapital("Mozambique", "Maputo", "MZ"),
        CountryCapital("Myanmar", "Naypyidaw", "MM"),
        CountryCapital("Namibia", "Windhoek", "NA"),
        CountryCapital("Nauru", "Yaren District", "NR"),
        CountryCapital("Nepal", "Kathmandu", "NP"),
        CountryCapital("Netherlands", "Amsterdam", "NL"),
        CountryCapital("New Zealand", "Wellington", "NZ"),
        CountryCapital("Nicaragua", "Managua", "NI"),
        CountryCapital("Niger", "Niamey", "NE"),
        CountryCapital("Nigeria", "Abuja", "NG"),
        CountryCapital("North Korea", "Pyongyang", "KP"),
        CountryCapital("North Macedonia", "Skopje", "MK"),
        CountryCapital("Norway", "Oslo", "NO"),
        CountryCapital("Oman", "Muscat", "OM"),
        CountryCapital("Pakistan", "Islamabad", "PK"),
        CountryCapital("Palau", "Ngerulmud", "PW"),
        CountryCapital("Panama", "Panama City", "PA"),
        CountryCapital("Papua New Guinea", "Port Moresby", "PG"),
        CountryCapital("Paraguay", "Asuncion", "PY"),
        CountryCapital("Peru", "Lima", "PE"),
        CountryCapital("Philippines", "Manila", "PH"),
        CountryCapital("Poland", "Warsaw", "PL"),
        CountryCapital("Portugal", "Lisbon", "PT"),
        CountryCapital("Qatar", "Doha", "QA"),
        CountryCapital("Romania", "Bucharest", "RO"),
        CountryCapital("Russia", "Moscow", "RU"),
        CountryCapital("Rwanda", "Kigali", "RW"),
        CountryCapital("Saint Kitts and Nevis", "Basseterre", "KN"),
        CountryCapital("Saint Lucia", "Castries", "LC"),
        CountryCapital("Saint Vincent and the Grenadines", "Kingstown", "VC"),
        CountryCapital("Samoa", "Apia", "WS"),
        CountryCapital("San Marino", "San Marino", "SM"),
        CountryCapital("Sao Tome and Principe", "Sao Tome", "ST"),
        CountryCapital("Saudi Arabia", "Riyadh", "SA"),
        CountryCapital("Senegal", "Dakar", "SN"),
        CountryCapital("Serbia", "Belgrade", "RS"),
        CountryCapital("Seychelles", "Victoria", "SC"),
        CountryCapital("Sierra Leone", "Freetown", "SL"),
        CountryCapital("Singapore", "Singapore", "SG"),
        CountryCapital("Slovakia", "Bratislava", "SK"),
        CountryCapital("Slovenia", "Ljubljana", "SI"),
        CountryCapital("Solomon Islands", "Honiara", "SB"),
        CountryCapital("Somalia", "Mogadishu", "SO"),
        CountryCapital("South Africa", "Pretoria", "ZA"),
        CountryCapital("South Korea", "Seoul", "KR"),
        CountryCapital("South Sudan", "Juba", "SS"),
        CountryCapital("Spain", "Madrid", "ES"),
        CountryCapital("Sri Lanka", "Sri Jayawardenepura Kotte", "LK"),
        CountryCapital("Sudan", "Khartoum", "SD"),
        CountryCapital("Suriname", "Paramaribo", "SR"),
        CountryCapital("Sweden", "Stockholm", "SE"),
        CountryCapital("Switzerland", "Bern", "CH"),
        CountryCapital("Syria", "Damascus", "SY"),
        CountryCapital("Taiwan", "Taipei", "TW"),
        CountryCapital("Tajikistan", "Dushanbe", "TJ"),
        CountryCapital("Tanzania", "Dodoma", "TZ"),
        CountryCapital("Thailand", "Bangkok", "TH"),
        CountryCapital("Timor-Leste", "Dili", "TL"),
        CountryCapital("Togo", "Lome", "TG"),
        CountryCapital("Tonga", "Nuku'alofa", "TO"),
        CountryCapital("Trinidad and Tobago", "Port of Spain", "TT"),
        CountryCapital("Tunisia", "Tunis", "TN"),
        CountryCapital("Turkey", "Ankara", "TR"),
        CountryCapital("Turkmenistan", "Ashgabat", "TM"),
        CountryCapital("Tuvalu", "Funafuti", "TV"),
        CountryCapital("Uganda", "Kampala", "UG"),
        CountryCapital("Ukraine", "Kyiv", "UA"),
        CountryCapital("United Arab Emirates", "Abu Dhabi", "AE"),
        CountryCapital("United Kingdom", "London", "GB"),
        CountryCapital("United States", "Washington, D.C.", "US"),
        CountryCapital("Uruguay", "Montevideo", "UY"),
        CountryCapital("Uzbekistan", "Tashkent", "UZ"),
        CountryCapital("Vanuatu", "Port Vila", "VU"),
        CountryCapital("Vatican City", "Vatican City", "VA"),
        CountryCapital("Venezuela", "Caracas", "VE"),
        CountryCapital("Vietnam", "Hanoi", "VN"),
        CountryCapital("Yemen", "Sanaa", "YE"),
        CountryCapital("Zambia", "Lusaka", "ZM"),
        CountryCapital("Zimbabwe", "Harare", "ZW")
    ).sortedBy { it.country }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capitals)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        etSearch = findViewById(R.id.etSearchCapitals)
        rvCapitals = findViewById(R.id.rvCapitals)
        rvCapitals.layoutManager = LinearLayoutManager(this)
        
        val adapter = CapitalsAdapter(allCapitals)
        rvCapitals.adapter = adapter

        etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase(Locale.getDefault())
            val filtered = allCapitals.filter { 
                it.country.lowercase(Locale.getDefault()).contains(query) || 
                it.capital.lowercase(Locale.getDefault()).contains(query)
            }
            adapter.updateList(filtered)
        }
    }

    data class CountryCapital(val country: String, val capital: String, val countryCode: String)

    class CapitalsAdapter(private var list: List<CountryCapital>) : RecyclerView.Adapter<CapitalsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvCountry: TextView = view.findViewById(R.id.tvCityName)
            val tvCapital: TextView = view.findViewById(R.id.tvCityTime)
            val tvFlag: TextView = view.findViewById(R.id.tvFlag)
        }

        fun updateList(newList: List<CountryCapital>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_world_clock, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.tvCountry.text = item.country
            holder.tvCapital.text = item.capital
            holder.tvCapital.setTextColor(0xFF7F00FF.toInt()) // Violet
            holder.tvFlag.text = countryCodeToEmoji(item.countryCode)
        }

        override fun getItemCount() = list.size

        private fun countryCodeToEmoji(countryCode: String): String {
            val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }
    }
}
