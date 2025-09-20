package com.example.finalassign4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIChartView
import com.highsoft.highcharts.core.HIFunction

class Fragment3 : Fragment() {

    private var currentHumidity: String? = null
    private var currentCloudCover: String? = null
    private var currentPrecipitation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentHumidity = it.getString("currentHumidity")
            currentCloudCover = it.getString("currentCloudCover")
            currentPrecipitation = it.getString("currentPrecipitation")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_3, container, false)
        val chartView = view.findViewById<HIChartView>(R.id.hc2)
        val options = HIOptions()

        // Chart setup
        val chart = HIChart().apply {
            type = "solidgauge"
            events = HIEvents().apply {
                render = HIFunction(renderIconsString)
            }
        }
        options.chart = chart

        // Chart title
        val title = HITitle().apply {
            text = "Stat Summary"
            style = HICSSObject().apply { fontSize = "24px" }
        }
        options.title = title

        // Tooltip configuration
        val tooltip = HITooltip().apply {
            borderWidth = 0
            backgroundColor = HIColor.initWithName("none")
            style = HICSSObject().apply { fontSize = "16px" }
            pointFormat =
                "{series.name}<br><span style=\"font-size:2em; color: {point.color}; font-weight: bold\">{point.y}%</span>"
        }
        options.tooltip = tooltip

        // Pane configuration
        val pane = HIPane().apply {
            startAngle = 0
            endAngle = 360
            background = arrayListOf(
                HIBackground().apply {
                    outerRadius = "112%"
                    innerRadius = "88%"
                    backgroundColor = HIColor.initWithRGBA(179, 229, 180, 0.35) // Light green
                    borderWidth = 0
                },
                HIBackground().apply {
                    outerRadius = "87%"
                    innerRadius = "63%"
                    backgroundColor = HIColor.initWithRGBA(166, 201, 226, 0.35) // Light blue
                    borderWidth = 0
                },
                HIBackground().apply {
                    outerRadius = "62%"
                    innerRadius = "38%"
                    backgroundColor = HIColor.initWithRGBA(242, 142, 115, 0.35) // Light orange
                    borderWidth = 0
                }
            )
        }
        options.pane = arrayListOf(pane)

        // Y-axis configuration
        val yAxis = HIYAxis().apply {
            min = 0
            max = 100
            lineWidth = 0
            tickPositions = arrayListOf()
        }
        options.yAxis = arrayListOf(yAxis)

        // Plot options
        val plotOptions = HIPlotOptions().apply {
            solidgauge = HISolidgauge().apply {
                dataLabels = arrayListOf(HIDataLabels().apply { enabled = false })
                linecap = "round"
                stickyTracking = false
                rounded = true
            }
        }
        options.plotOptions = plotOptions

        // Series data
        val cloudCover = HISolidgauge().apply {
            name = "Cloud Cover"
            data = arrayListOf(
                HIData().apply {
                    color = HIColor.initWithRGB(179, 229, 180)
                    radius = "112%"
                    innerRadius = "88%"
                    y = currentCloudCover?.toDoubleOrNull()?.toInt() ?: 70
                }
            )
        }

        val precipitation = HISolidgauge().apply {
            name = "Precipitation"
            data = arrayListOf(
                HIData().apply {
                    color = HIColor.initWithRGB(166, 201, 226)
                    radius = "87%"
                    innerRadius = "63%"
                    y = currentPrecipitation?.toDoubleOrNull()?.toInt() ?: 60
                }
            )
        }

        val humidity = HISolidgauge().apply {
            name = "Humidity"
            data = arrayListOf(
                HIData().apply {
                    color = HIColor.initWithRGB(242, 142, 115)
                    radius = "62%"
                    innerRadius = "38%"
                    y = currentHumidity?.toDoubleOrNull()?.toInt() ?: 50
                }
            )
        }

        options.series = arrayListOf(cloudCover, precipitation, humidity)

        // Assign options to chartView
        chartView.options = options
        return view
    }

    // Render function for adding icons on top of the gauges
    private val  renderIconsString = "function renderIcons() {" +
    "                            if(!this.series[0].icon) {" +
    "                               this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[0].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -(this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2); if(!this.series[1].icon) {this.series[1].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,'M', 8, -8, 'L', 16, 0, 8, 8]).attr({'stroke': '#ffffff','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[1].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -(this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2); if(!this.series[2].icon) {this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[2].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -(this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2);}";

}
