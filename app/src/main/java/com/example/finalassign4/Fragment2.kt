package com.example.finalassign4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.HIGradient
import com.highsoft.highcharts.common.HIStop
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIChartView
import org.json.JSONArray
import java.util.LinkedList

data class WeatherData(
    val startTime: Long,
    val values: TemperatureValues
)

// Data class for the "values" object
data class TemperatureValues(
    val temperature: Double,
    val temperatureMax: Double,
    val temperatureMin: Double
)
class Fragment2 : Fragment() {
    private lateinit var progressOverlay: FrameLayout
    private val BACKEND_URL = "http://10.0.0.202:3000"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_2, container, false)
        val chartView = view.findViewById<HIChartView>(R.id.hc)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressOverlay = view.findViewById(R.id.progress_overlay)

        fetchDataAndUpdateChart(chartView, progressBar)
        // Find the HIChartView in your layout


        return view
    }
    private fun updateChart(chartView: HIChartView, weatherDataList: List<WeatherData>) {
        // Set up chart options
        val options = HIOptions()
        options.colors = arrayListOf("#6CF", "#39F", "#06C", "#036", "#000")
        // Configure the chart
        val chart = HIChart()
        val zoomType = HIZooming()
        zoomType.pinchType = "x"
        chart.zooming = zoomType
        chart.type = "arearange"
        options.chart = chart

        // Set chart title
        val title = HITitle()
        title.text = "Temperature variation by day"
        options.title = title

        // Configure the X-axis
        val xAxis = HIXAxis()
        xAxis.type = "datetime"
        options.xAxis = arrayListOf(xAxis)

        // Configure the Y-axis
        val yAxis = HIYAxis()
        yAxis.title = HITitle().apply { text = "Values" }
        options.yAxis = arrayListOf(yAxis)

        // Configure the Tooltip
        val tooltip = HITooltip()
        tooltip.valueSuffix = "°F"
        options.tooltip = tooltip

        // Disable the Legend
        val legend = HILegend()
        legend.enabled = false
        options.legend = legend

        // Create the Area Range series
        val series = HIArearange()
        series.name = "Temperatures"

        // Prepare the data
        val seriesData = ArrayList<Array<Any?>>()
        for (data in weatherDataList) {
            seriesData.add(arrayOf(data.startTime, data.values.temperatureMin, data.values.temperatureMax))
        }
        series.data = seriesData

        // Define stops for the gradient
        val gradient = HIGradient(0f, 0f, 0f, 1f) // x1=0, y1=0, x2=0, y2=1 (vertical gradient)

// Define stops for the gradient
        val stops = LinkedList<HIStop>()
        stops.add(HIStop(0f, HIColor.initWithName("orange")))    // Stop at position 0: orange color
        stops.add(HIStop(1f, HIColor.initWithName("blue")))   // Stop at position 1: skyblue color
        series.color = HIColor.initWithLinearGradient(gradient, stops)
        // Add series to the options
        options.series = arrayListOf(series)
        // Add series to the options
        options.series = arrayListOf(series)
        // Apply options to the chart view
        chartView.options = options
    }


private fun fetchDataAndUpdateChart(chartView: HIChartView, progressBar: ProgressBar) {
    val url = "${BACKEND_URL}/fiveD_weather_info?latitude=2&longitude=5"
    val requestQueue = Volley.newRequestQueue(context)
    val weatherDataList = ArrayList<WeatherData>()
    progressOverlay.visibility = View.VISIBLE
    chartView.visibility = View.GONE
    val jsonArrayRequest = JsonArrayRequest(
        Request.Method.GET, url, null,
        { response ->
            Log.d("FetchData", "Raw Response: $response")
            Log.d("FetchData", "Response received, processing data...")

            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                val startTime = item.getString("startTime")
                val values = item.getJSONObject("values")

                val weatherData = WeatherData(
                    startTime = convertToMillis(startTime),
                    values = TemperatureValues(
                        temperature = values.getDouble("temperature"),
                        temperatureMax = values.getDouble("temperatureMax"),
                        temperatureMin = values.getDouble("temperatureMin")
                    )
                )
                weatherDataList.add(weatherData)
            }

            Log.d("FetchData", "Parsed Weather Data: $weatherDataList")

            Handler(Looper.getMainLooper()).postDelayed({
                activity?.runOnUiThread {
                    if (weatherDataList.isNotEmpty()) {
                        updateChart(chartView, weatherDataList) // Update chart with data
                        chartView.visibility = View.VISIBLE // Show the chart

                        Log.d("FetchData", "Chart updated successfully.")
                    } else {
                        Log.e("FetchData", "No data to display on the chart!")
                    }
                    progressOverlay.visibility = View.GONE
                }
            }, 1000)
        },
        { error ->
            error.printStackTrace()
            Log.e("FetchData", "Error fetching data: ${error.message}")
            activity?.runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }
    )

    progressBar.visibility = View.VISIBLE
    Log.d("FetchData", "Fetching data from $url...")
    requestQueue.add(jsonArrayRequest)
}


    private fun convertToMillis(dateStr: String): Long {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US)
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateStr) ?: return 0
        return date.time
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment2().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
