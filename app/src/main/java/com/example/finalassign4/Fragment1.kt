package com.example.finalassign4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class Fragment1 : Fragment() {

    private var currentLocation: String? = null
    private var currentLongitude: String? = null
    private var currentLatitude: String? = null
    private val BACKEND_URL = "http://10.0.0.202:3000"

    private lateinit var requestQueue: RequestQueue

    // Views
    private lateinit var temperatureTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var precipitationTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var cloudCoverTextView: TextView
    private lateinit var ozoneTextView: TextView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var weatherIcon: ImageView

    private lateinit var progressOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentLocation = it.getString("currentLocation")
            currentLongitude = it.getString("currentLongitude")
            currentLatitude = it.getString("currentLatitude")
        }
        requestQueue = Volley.newRequestQueue(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_1, container, false)

        // Initialize Views
        temperatureTextView = view.findViewById(R.id.temperature_value)
        windSpeedTextView = view.findViewById(R.id.windSpeed_value)
        pressureTextView = view.findViewById(R.id.pressure_value)
        precipitationTextView = view.findViewById(R.id.precipitation)
        humidityTextView = view.findViewById(R.id.humidity_value)
        visibilityTextView = view.findViewById(R.id.visibility_value)
        cloudCoverTextView = view.findViewById(R.id.cloud_cover_value)
        ozoneTextView = view.findViewById(R.id.ozone_value)
        weatherDescriptionTextView = view.findViewById(R.id.weather_details)
        weatherIcon = view.findViewById(R.id.weather_icon)

        // Progress Overlay
        progressOverlay = view.findViewById(R.id.progress_overlay)

        fetchWeather(currentLatitude ?: "0.0", currentLongitude ?: "0.0")
        return view
    }

    private fun fetchWeather(latitude: String, longitude: String) {
        val url = "$BACKEND_URL/today_weather_info?latitude=$latitude&longitude=$longitude"

        // Show progress overlay
        progressOverlay.visibility = View.VISIBLE

        val weatherRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Add a delay before hiding progress overlay
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressOverlay.visibility = View.GONE

                        val temperature = response.optDouble("temperature", 0.0).toInt().toString() + "°F"
                        val windSpeed = response.optDouble("windSpeed", 0.0).toString() + " mph"
                        val pressure = response.optDouble("pressureSeaLevel", 0.0).toString() + " inHg"
                        val precipitation = response.optDouble("precipitationProbability", 0.0).toString() + "%"
                        val humidity = response.optInt("humidity", 0).toString() + "%"
                        val visibility = response.optDouble("visibility", 0.0).toString() + " mi"
                        val cloudCover = response.optDouble("cloudCover", 0.0).toString() + "%"
                        val ozone = response.optDouble("uvIndex", 0.0).toString()
                        val weatherCode = response.optString("weatherCode")

                        // Update UI
                        temperatureTextView.text = temperature
                        windSpeedTextView.text = windSpeed
                        pressureTextView.text = pressure
                        precipitationTextView.text = precipitation
                        humidityTextView.text = humidity
                        visibilityTextView.text = visibility
                        cloudCoverTextView.text = cloudCover
                        ozoneTextView.text = ozone
                        weatherDescriptionTextView.text = WEATHER_CODE_ICON_MAP[weatherCode]?.first ?: "Unknown"
                        weatherIcon.setImageResource(WEATHER_CODE_ICON_MAP[weatherCode]?.second ?: R.drawable.clear_day)

                        Log.d("Weather", "Weather fetched successfully.")
                    }, 1000) // 1 second delay
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressOverlay.visibility = View.GONE
                    }, 1000) // 1 second delay
                    Log.e("Weather", "Error parsing weather data: ${e.message}")
                }
            },
            { error ->
                Handler(Looper.getMainLooper()).postDelayed({
                    progressOverlay.visibility = View.GONE
                }, 1000) // 1 second delay
                Log.e("Weather", "Error fetching weather data: ${error.message}")
            }
        )

        requestQueue.add(weatherRequest)
    }

    companion object {
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

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment1().apply {
                arguments = Bundle().apply {
                    putString("currentLongitude", param1)
                    putString("currentLatitude", param2)
                }
            }
    }
}
