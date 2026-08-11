package com.tahir.scical

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.text.SimpleDateFormat
import java.util.*

class WorldClockActivity : BaseCalculatorActivity() {

    private lateinit var rvWorldClock: RecyclerView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTask: Runnable
    private lateinit var adapter: WorldClockAdapter

    private val allCities = listOf(
        CityTime("Kabul", "Asia/Kabul", "AF"),
        CityTime("Tirana", "Europe/Tirane", "AL"),
        CityTime("Algiers", "Africa/Algiers", "DZ"),
        CityTime("Andorra la Vella", "Europe/Andorra", "AD"),
        CityTime("Luanda", "Africa/Luanda", "AO"),
        CityTime("Saint John's", "America/Antigua", "AG"),
        CityTime("Buenos Aires", "America/Argentina/Buenos_Aires", "AR"),
        CityTime("Yerevan", "Asia/Yerevan", "AM"),
        CityTime("Canberra", "Australia/Canberra", "AU"),
        CityTime("Vienna", "Europe/Vienna", "AT"),
        CityTime("Baku", "Asia/Baku", "AZ"),
        CityTime("Nassau", "America/Nassau", "BS"),
        CityTime("Manama", "Asia/Bahrain", "BH"),
        CityTime("Dhaka", "Asia/Dhaka", "BD"),
        CityTime("Bridgetown", "America/Barbados", "BB"),
        CityTime("Minsk", "Europe/Minsk", "BY"),
        CityTime("Brussels", "Europe/Brussels", "BE"),
        CityTime("Belmopan", "America/Belize", "BZ"),
        CityTime("Porto-Novo", "Africa/Porto-Novo", "BJ"),
        CityTime("Thimphu", "Asia/Thimphu", "BT"),
        CityTime("Sucre", "America/La_Paz", "BO"),
        CityTime("Sarajevo", "Europe/Sarajevo", "BA"),
        CityTime("Gaborone", "Africa/Gaborone", "BW"),
        CityTime("Brasilia", "America/Sao_Paulo", "BR"),
        CityTime("Bandar Seri Begawan", "Asia/Brunei", "BN"),
        CityTime("Sofia", "Europe/Sofia", "BG"),
        CityTime("Ouagadougou", "Africa/Ouagadougou", "BF"),
        CityTime("Gitega", "Africa/Bujumbura", "BI"),
        CityTime("Praia", "Atlantic/Cape_Verde", "CV"),
        CityTime("Phnom Penh", "Asia/Phnom_Penh", "KH"),
        CityTime("Yaounde", "Africa/Douala", "CM"),
        CityTime("Ottawa", "America/Toronto", "CA"),
        CityTime("Bangui", "Africa/Bangui", "CF"),
        CityTime("N'Djamena", "Africa/Ndjamena", "TD"),
        CityTime("Santiago", "America/Santiago", "CL"),
        CityTime("Beijing", "Asia/Shanghai", "CN"),
        CityTime("Bogota", "America/Bogota", "CO"),
        CityTime("Moroni", "Indian/Comoro", "KM"),
        CityTime("Brazzaville", "Africa/Brazzaville", "CG"),
        CityTime("Kinshasa", "Africa/Kinshasa", "CD"),
        CityTime("San Jose", "America/Costa_Rica", "CR"),
        CityTime("Yamoussoukro", "Africa/Abidjan", "CI"),
        CityTime("Zagreb", "Europe/Zagreb", "HR"),
        CityTime("Havana", "America/Havana", "CU"),
        CityTime("Nicosia", "Asia/Nicosia", "CY"),
        CityTime("Prague", "Europe/Prague", "CZ"),
        CityTime("Copenhagen", "Europe/Copenhagen", "DK"),
        CityTime("Djibouti", "Africa/Djibouti", "DJ"),
        CityTime("Roseau", "America/Dominica", "DM"),
        CityTime("Santo Domingo", "America/Santo_Domingo", "DO"),
        CityTime("Quito", "America/Guayaquil", "EC"),
        CityTime("Cairo", "Africa/Cairo", "EG"),
        CityTime("San Salvador", "America/El_Salvador", "SV"),
        CityTime("Malabo", "Africa/Malabo", "GQ"),
        CityTime("Asmara", "Africa/Asmara", "ER"),
        CityTime("Tallinn", "Europe/Tallinn", "EE"),
        CityTime("Mbabane", "Africa/Mbabane", "SZ"),
        CityTime("Addis Ababa", "Africa/Addis_Ababa", "ET"),
        CityTime("Palikir", "Pacific/Pohnpei", "FM"),
        CityTime("Suva", "Pacific/Fiji", "FJ"),
        CityTime("Helsinki", "Europe/Helsinki", "FI"),
        CityTime("Paris", "Europe/Paris", "FR"),
        CityTime("Libreville", "Africa/Libreville", "GA"),
        CityTime("Banjul", "Africa/Banjul", "GM"),
        CityTime("Tbilisi", "Asia/Tbilisi", "GE"),
        CityTime("Berlin", "Europe/Berlin", "DE"),
        CityTime("Accra", "Africa/Accra", "GH"),
        CityTime("Athens", "Europe/Athens", "GR"),
        CityTime("Saint George's", "America/Grenada", "GD"),
        CityTime("Guatemala City", "America/Guatemala", "GT"),
        CityTime("Conakry", "Africa/Conakry", "GN"),
        CityTime("Bissau", "Africa/Bissau", "GW"),
        CityTime("Georgetown", "America/Guyana", "GY"),
        CityTime("Port-au-Prince", "America/Port-au-Prince", "HT"),
        CityTime("Tegucigalpa", "America/Tegucigalpa", "HN"),
        CityTime("Budapest", "Europe/Budapest", "HU"),
        CityTime("Reykjavik", "Atlantic/Reykjavik", "IS"),
        CityTime("New Delhi", "Asia/Kolkata", "IN"),
        CityTime("Jakarta", "Asia/Jakarta", "ID"),
        CityTime("Tehran", "Asia/Tehran", "IR"),
        CityTime("Baghdad", "Asia/Baghdad", "IQ"),
        CityTime("Dublin", "Europe/Dublin", "IE"),
        CityTime("Rome", "Europe/Rome", "IT"),
        CityTime("Kingston", "America/Jamaica", "JM"),
        CityTime("Tokyo", "Asia/Tokyo", "JP"),
        CityTime("Amman", "Asia/Amman", "JO"),
        CityTime("Astana", "Asia/Almaty", "KZ"),
        CityTime("Nairobi", "Africa/Nairobi", "KE"),
        CityTime("Tarawa", "Pacific/Tarawa", "KI"),
        CityTime("Pristina", "Europe/Belgrade", "XK"),
        CityTime("Kuwait City", "Asia/Kuwait", "KW"),
        CityTime("Bishkek", "Asia/Bishkek", "KG"),
        CityTime("Vientiane", "Asia/Vientiane", "LA"),
        CityTime("Riga", "Europe/Riga", "LV"),
        CityTime("Beirut", "Asia/Beirut", "LB"),
        CityTime("Maseru", "Africa/Maseru", "LS"),
        CityTime("Monrovia", "Africa/Monrovia", "LR"),
        CityTime("Tripoli", "Africa/Tripoli", "LY"),
        CityTime("Vaduz", "Europe/Vaduz", "LI"),
        CityTime("Vilnius", "Europe/Vilnius", "LT"),
        CityTime("Luxembourg", "Europe/Luxembourg", "LU"),
        CityTime("Antananarivo", "Indian/Antananarivo", "MG"),
        CityTime("Lilongwe", "Africa/Lilongwe", "MW"),
        CityTime("Kuala Lumpur", "Asia/Kuala_Lumpur", "MY"),
        CityTime("Male", "Indian/Maldives", "MV"),
        CityTime("Bamako", "Africa/Bamako", "ML"),
        CityTime("Valletta", "Europe/Malta", "MT"),
        CityTime("Majuro", "Pacific/Majuro", "MH"),
        CityTime("Nouakchott", "Africa/Nouakchott", "MR"),
        CityTime("Port Louis", "Indian/Mauritius", "MU"),
        CityTime("Mexico City", "America/Mexico_City", "MX"),
        CityTime("Chisinau", "Europe/Chisinau", "MD"),
        CityTime("Monaco", "Europe/Monaco", "MC"),
        CityTime("Ulaanbaatar", "Asia/Ulaanbaatar", "MN"),
        CityTime("Podgorica", "Europe/Podgorica", "ME"),
        CityTime("Rabat", "Africa/Casablanca", "MA"),
        CityTime("Maputo", "Africa/Maputo", "MZ"),
        CityTime("Naypyidaw", "Asia/Yangon", "MM"),
        CityTime("Windhoek", "Africa/Windhoek", "NA"),
        CityTime("Yaren", "Pacific/Nauru", "NR"),
        CityTime("Kathmandu", "Asia/Kathmandu", "NP"),
        CityTime("Amsterdam", "Europe/Amsterdam", "NL"),
        CityTime("Wellington", "Pacific/Auckland", "NZ"),
        CityTime("Managua", "America/Managua", "NI"),
        CityTime("Niamey", "Africa/Niamey", "NE"),
        CityTime("Abuja", "Africa/Lagos", "NG"),
        CityTime("Skopje", "Europe/Skopje", "MK"),
        CityTime("Oslo", "Europe/Oslo", "NO"),
        CityTime("Muscat", "Asia/Muscat", "OM"),
        CityTime("Islamabad", "Asia/Karachi", "PK"),
        CityTime("Ngerulmud", "Pacific/Palau", "PW"),
        CityTime("Panama City", "America/Panama", "PA"),
        CityTime("Port Moresby", "Pacific/Port_Moresby", "PG"),
        CityTime("Asuncion", "America/Asuncion", "PY"),
        CityTime("Lima", "America/Lima", "PE"),
        CityTime("Manila", "Asia/Manila", "PH"),
        CityTime("Warsaw", "Europe/Warsaw", "PL"),
        CityTime("Lisbon", "Europe/Lisbon", "PT"),
        CityTime("Doha", "Asia/Qatar", "QA"),
        CityTime("Bucharest", "Europe/Bucharest", "RO"),
        CityTime("Moscow", "Europe/Moscow", "RU"),
        CityTime("Kigali", "Africa/Kigali", "RW"),
        CityTime("Basseterre", "America/St_Kitts", "KN"),
        CityTime("Castries", "America/St_Lucia", "LC"),
        CityTime("Kingstown", "America/St_Vincent", "VC"),
        CityTime("Apia", "Pacific/Apia", "WS"),
        CityTime("San Marino", "Europe/San_Marino", "SM"),
        CityTime("Sao Tome", "Africa/Sao_Tome", "ST"),
        CityTime("Riyadh", "Asia/Riyadh", "SA"),
        CityTime("Dakar", "Africa/Dakar", "SN"),
        CityTime("Belgrade", "Europe/Belgrade", "RS"),
        CityTime("Victoria", "Indian/Mahe", "SC"),
        CityTime("Freetown", "Africa/Freetown", "SL"),
        CityTime("Singapore", "Asia/Singapore", "SG"),
        CityTime("Bratislava", "Europe/Bratislava", "SK"),
        CityTime("Ljubljana", "Europe/Ljubljana", "SI"),
        CityTime("Honiara", "Pacific/Guadalcanal", "SB"),
        CityTime("Mogadishu", "Africa/Mogadishu", "SO"),
        CityTime("Pretoria", "Africa/Johannesburg", "ZA"),
        CityTime("Seoul", "Asia/Seoul", "KR"),
        CityTime("Juba", "Africa/Juba", "SS"),
        CityTime("Madrid", "Europe/Madrid", "ES"),
        CityTime("Sri Jayawardenepura Kotte", "Asia/Colombo", "LK"),
        CityTime("Khartoum", "Africa/Khartoum", "SD"),
        CityTime("Paramaribo", "America/Paramaribo", "SR"),
        CityTime("Stockholm", "Europe/Stockholm", "SE"),
        CityTime("Bern", "Europe/Zurich", "CH"),
        CityTime("Damascus", "Asia/Damascus", "SY"),
        CityTime("Taipei", "Asia/Taipei", "TW"),
        CityTime("Dushanbe", "Asia/Dushanbe", "TJ"),
        CityTime("Dodoma", "Africa/Dar_es_Salaam", "TZ"),
        CityTime("Bangkok", "Asia/Bangkok", "TH"),
        CityTime("Dili", "Asia/Dili", "TL"),
        CityTime("Lome", "Africa/Lome", "TG"),
        CityTime("Nuku'alofa", "Pacific/Tongatapu", "TO"),
        CityTime("Port of Spain", "America/Port_of_Spain", "TT"),
        CityTime("Tunis", "Africa/Tunis", "TN"),
        CityTime("Ankara", "Europe/Istanbul", "TR"),
        CityTime("Ashgabat", "Asia/Ashgabat", "TM"),
        CityTime("Funafuti", "Pacific/Funafuti", "TV"),
        CityTime("Kampala", "Africa/Kampala", "UG"),
        CityTime("Kyiv", "Europe/Kyiv", "UA"),
        CityTime("Abu Dhabi", "Asia/Dubai", "AE"),
        CityTime("London", "Europe/London", "GB"),
        CityTime("Washington, D.C.", "America/New_York", "US"),
        CityTime("Montevideo", "America/Montevideo", "UY"),
        CityTime("Tashkent", "Asia/Tashkent", "UZ"),
        CityTime("Port Vila", "Pacific/Efate", "VU"),
        CityTime("Vatican City", "Europe/Vatican", "VA"),
        CityTime("Caracas", "America/Caracas", "VE"),
        CityTime("Hanoi", "Asia/Ho_Chi_Minh", "VN"),
        CityTime("Sana'a", "Asia/Aden", "YE"),
        CityTime("Lusaka", "Africa/Lusaka", "ZM"),
        CityTime("Harare", "Africa/Harare", "ZW")
    ).sortedBy { it.name }

    private var filteredCities = allCities.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_clock)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        rvWorldClock = findViewById(R.id.rvWorldClock)
        rvWorldClock.layoutManager = LinearLayoutManager(this)
        adapter = WorldClockAdapter(filteredCities)
        rvWorldClock.adapter = adapter

        setupSearch()

        updateTask = object : Runnable {
            override fun run() {
                adapter.notifyDataSetChanged()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateTask)
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearchWorldClock)
        etSearch.addTextChangedListener { text ->
            val query = text.toString().lowercase(Locale.getDefault())
            filteredCities.clear()
            if (query.isEmpty()) {
                filteredCities.addAll(allCities)
            } else {
                filteredCities.addAll(allCities.filter { 
                    it.name.lowercase(Locale.getDefault()).contains(query) || 
                    it.timeZoneId.lowercase(Locale.getDefault()).contains(query)
                })
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTask)
    }

    data class CityTime(val name: String, val timeZoneId: String, val countryCode: String)

    class WorldClockAdapter(private val cities: List<CityTime>) : RecyclerView.Adapter<WorldClockAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvCity: TextView = view.findViewById(R.id.tvCityName)
            val tvTime: TextView = view.findViewById(R.id.tvCityTime)
            val ivFlag: TextView = view.findViewById(R.id.tvFlag)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_world_clock, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val city = cities[position]
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone(city.timeZoneId)
            
            holder.tvCity.text = city.name
            holder.tvTime.text = sdf.format(Date())
            holder.ivFlag.text = countryCodeToEmoji(city.countryCode)
        }

        override fun getItemCount() = cities.size

        private fun countryCodeToEmoji(countryCode: String): String {
            val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }
    }
}
