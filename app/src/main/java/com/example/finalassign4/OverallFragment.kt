package com.example.finalassign4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverallFragment : Fragment() {
    private val BACKEND_URL = "http://10.0.0.202:3000"
    private val GEOCODING_API_KEY = "AIzaSyA8PLBJrghiHuMpMjhFQ3LVgTuuGZ10m0k"
    private val GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json"

    private var latitude: String? = null
    private var longitude: String? = null
    private var locationStr: String? = null

    private lateinit var progressBar: ProgressBar

    private lateinit var rootView: View
    private lateinit var locationTextView: TextView
    private lateinit var progressOverlay: View
    private lateinit var temperatureTextView: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var weatherDescription: TextView
    private lateinit var currentPrecipitation: String
    private lateinit var currentCloudCover: String
    private lateinit var currentHumidity: String

    private lateinit var floatingButton: FloatingActionButton

    private lateinit var requestQueue: RequestQueue

    // Define a global weatherCode-to-icon map
    val WEATHER_CODE_ICON_MAP = mapOf(
        "4201" to Pair("Heavy Rain", R.drawable.rain_heavy),
        "4001" to Pair("Rain", R.drawable.rain),
        "4200" to Pair("Light Rain", R.drawable.rain_light),
        "6201" to Pair("Heavy Freezing Rain", R.drawable.freezing_rain_heavy),
        "6001" to Pair("Freezing Rain", R.drawable.freezing_rain),
        "6200" to Pair("Light Freezing Rain", R.drawable.freezing_rain_light),
        "6000" to Pair("Freezing Drizzle", R.drawable.freezing_drizzle),
        "4000" to Pair("Drizzle", R.drawable.drizzle),
        "7101" to Pair("Heavy Ice Pellets", R.drawable.ice_pellets_heavy),
        "7000" to Pair("Ice Pellets", R.drawable.ice_pellets),
        "7102" to Pair("Light Ice Pellets", R.drawable.ice_pellets_light),
        "5101" to Pair("Heavy Snow", R.drawable.snow_heavy),
        "5000" to Pair("Snow", R.drawable.snow),
        "5100" to Pair("Light Snow", R.drawable.snow_light),
        "5001" to Pair("Flurries", R.drawable.flurries),
        "8000" to Pair("Thunderstorm", R.drawable.tstorm),
        "2100" to Pair("Light Fog", R.drawable.fog_light),
        "2000" to Pair("Fog", R.drawable.fog),
        "1001" to Pair("Cloudy", R.drawable.cloudy),
        "1102" to Pair("Mostly Cloudy", R.drawable.mostly_cloudy),
        "1101" to Pair("Partly Cloudy", R.drawable.partly_cloudy_day),
        "1100" to Pair("Mostly Clear", R.drawable.mostly_clear_day),
        "1000" to Pair("Clear, Sunny", R.drawable.clear_day)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getString(ARG_PARAM1)
            longitude = it.getString(ARG_PARAM2)
            locationStr = it.getString("locationStr")
        }

        requestQueue = Volley.newRequestQueue(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_overall, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        locationTextView = view.findViewById(R.id.location)
        temperatureTextView = view.findViewById(R.id.temperature)
        humidityTextView = view.findViewById(R.id.humidity)
        windSpeedTextView = view.findViewById(R.id.windSpeed)
        visibilityTextView = view.findViewById(R.id.visibility)
        pressureTextView = view.findViewById(R.id.pressure)
        weatherIcon = view.findViewById(R.id.weatherIcon)
        weatherDescription = view.findViewById(R.id.weatherDescription)
        floatingButton = view.findViewById(R.id.fab)
        rootView = view.findViewById(R.id.root_layout)
        progressOverlay = view.findViewById(R.id.progress_overlay)


        floatingButton.visibility = View.VISIBLE

        view.findViewById<LinearLayout>(R.id.today_weather_section)
            .setOnClickListener(View.OnClickListener {
                val intent = Intent(context, DetailedViewActivity::class.java)
                intent.putExtra("currentLocation", locationTextView.text.toString())
                intent.putExtra("currentLatitude", latitude)
                intent.putExtra("currentLongitude", longitude)
                intent.putExtra("currentTemperature", temperatureTextView.text.toString())
                intent.putExtra("currentHumidity", humidityTextView.text.toString())
                intent.putExtra("currentPrecipitation", currentPrecipitation)
                intent.putExtra("currentCloudCover", currentCloudCover)
                intent.putExtra("currentHumidity", currentHumidity)

                startActivity(intent)
            })
//         Fetch weather based on the received latitude and longitudeQ
        if (latitude != "" && longitude != "") {
            locationTextView.text = locationStr
            fetchWeather(latitude!!, longitude!!)
        }
        else{
            fetchLocationAndWeather()
        }
        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param latitude Parameter 1.
         * @param longitude Parameter 2.
         * @return A new instance of fragment OverallFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(latitude: String, longitude: String, locationStr: String) =
            OverallFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, latitude)
                    putString(ARG_PARAM2, longitude)
                    putString("locationStr", locationStr)
                }
            }
    }

    private fun fetchLocationAndWeather() {
        val url = "https://ipinfo.io/json"

        rootView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val locationRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val loc = response.optString("loc", "0,0") // e.g., "37.7749,-122.4194"
                    val (latitude, longitude) = loc.split(",")
                    val city = response.optString("city", "Unknown City")
                    val region = response.optString("region", "Unknown Region")
                    locationTextView.text = "$city, $region"

                    // Fetch weather using latitude and longitude
                    fetchWeather(latitude, longitude)
                } catch (e: Exception) {
                    Log.e("IPInfo", "Error parsing location: ${e.message}")
                    Toast.makeText(context, "Error fetching location", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(
                    context,
                    "Failed to fetch location: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("IPInfo", "Error: ${error.message}")
            }
        )

        requestQueue.add(locationRequest)
    }

    private fun fetchWeather(latitude: String, longitude: String) {
        val url = "${BACKEND_URL}/today_weather_info?latitude=$latitude&longitude=$longitude"

        // Show progress overlay and hide root layout
        progressOverlay.visibility = View.VISIBLE
        rootView.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val weatherRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        // Hide progress overlay and show root layout
                        progressOverlay.visibility = View.GONE
                        rootView.visibility = View.VISIBLE

                        val temperature =
                            response.optDouble("temperature", 0.0).toInt().toString() + "°F"
                        val humidity = response.optInt("humidity", 0).toString() + "%"
                        val windSpeed = response.optDouble("windSpeed", 0.0).toString() + " mph"
                        val visibility = response.optDouble("visibility", 0.0).toString() + " mi"
                        val pressure =
                            response.optDouble("pressureSeaLevel", 0.0).toString() + " inHg"
                        val weatherCode = response.optString("weatherCode")

                        currentHumidity = response.optInt("humidity", 0).toString()
                        currentPrecipitation =
                            response.optDouble("precipitationProbability", 0.0).toString()
                        currentCloudCover = response.optDouble("cloudCover", 0.0).toString()

                        temperatureTextView.text = temperature
                        humidityTextView.text = humidity
                        windSpeedTextView.text = windSpeed
                        visibilityTextView.text = visibility
                        pressureTextView.text = pressure
                        weatherDescription.text =
                            WEATHER_CODE_ICON_MAP[weatherCode]?.first ?: "Unknown"
                        weatherIcon.setImageResource(
                            WEATHER_CODE_ICON_MAP[weatherCode]?.second ?: R.drawable.clear_day
                        )

                        fetchWeeklyWeather(latitude, longitude)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error fetching weather details",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressOverlay.visibility = View.GONE
                        rootView.visibility = View.VISIBLE
                    }
                },
                { error ->
                    Toast.makeText(
                        context,
                        "Failed to fetch weather: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressOverlay.visibility = View.GONE
                    rootView.visibility = View.VISIBLE
                }
            )

            requestQueue.add(weatherRequest)
        }, 100) // 2-second delay
    }

    private fun fetchWeeklyWeather(latitude: String, longitude: String) {
        val url = "${BACKEND_URL}/week_weather_info?latitude=$latitude&longitude=$longitude"
        val view = requireView()

        val rootView: View = view.findViewById(R.id.root_layout)
        val progressOverlay: View = view.findViewById(R.id.progress_overlay)

        // Show progress overlay and hide root layout
        progressOverlay.visibility = View.VISIBLE
        rootView.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val weeklyWeatherRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    progressOverlay.visibility = View.GONE
                    rootView.visibility = View.VISIBLE

                    try {
                        val forecastContainer: LinearLayout = view.findViewById(R.id.forecastContainer)
                        forecastContainer.removeAllViews()

                        for (i in 0 until response.length()) {
                            val dayForecast = response.getJSONObject(i)
                            val values = dayForecast.getJSONObject("values")
                            val startTime = dayForecast.optString("startTime")
                            val weatherCode = values.optString("weatherCode")
                            val temperature = values.optDouble("temperatureMin", 0.0).toInt().toString()
                            val humidity = values.optInt("temperatureMax", 0).toString()

                            val row = layoutInflater.inflate(R.layout.forecast_row, forecastContainer, false)
                            row.findViewById<TextView>(R.id.tvDate).text = startTime.split("T")[0]
                            row.findViewById<TextView>(R.id.tvTemperature).text = temperature
                            row.findViewById<TextView>(R.id.tvHumidity).text = humidity

                            val iconRes = WEATHER_CODE_ICON_MAP[weatherCode]?.second ?: R.drawable.clear_day
                            row.findViewById<ImageView>(R.id.rowWeatherIcon).setImageResource(iconRes)
                            forecastContainer.addView(row)
                        }
                    } catch (e: Exception) {
                        Log.e("WeeklyWeather", "Error parsing weekly forecast: ${e.message}")
                        Toast.makeText(context, "Error fetching weekly weather", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressOverlay.visibility = View.GONE
                    rootView.visibility = View.VISIBLE
                    Toast.makeText(context, "Failed to fetch weekly weather", Toast.LENGTH_SHORT).show()
                }
            )

            requestQueue.add(weeklyWeatherRequest)
        }, 100) // 2-second delay
    }
}