package com.example.finalassign4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailedViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_view)
        // Back Arrow Navigation Button
        val backArrowButton = findViewById<ImageView>(R.id.back_arrow)
        val twitterButton = findViewById<ImageView>(R.id.close_icon)
        backArrowButton.setOnClickListener {
            // Finish the current activity and navigate back
            finish()
        }
        val currentLocation = intent.getStringExtra("currentLocation")
        val currentLatitude = intent.getStringExtra("currentLatitude")
        val currentLogitude = intent.getStringExtra("currentLongitude")
        val currentTemperature = intent.getStringExtra("currentTemperature")

        val currentHumidity = intent.getStringExtra("currentHumidity")
        val currentCloudCover = intent.getStringExtra("currentCloudCover")
        val currentPrecipitation = intent.getStringExtra("currentPrecipitation")

        twitterButton.setOnClickListener {
            // URL you want to open
            val query = "Check out ${currentLocation}, USA's weather! It is ${currentTemperature}°F! #CSCI571WeatherSearch."
            val url = "https://twitter.com/intent/tweet?text=${Uri.encode(query)}"

            // Create an Intent to open a URL
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Receiving Input Text

        val textView = findViewById<TextView>(R.id.detail_text_current_location)
        textView.text = currentLocation ?: "No input provided"
        // ViewPager2 and TabLayout Integration
        val mViewPager: ViewPager2 = findViewById(R.id.my_view_pager)
        val mTabLayout: TabLayout = findViewById(R.id.my_tab_layout)
        val tabTitles = listOf("TODAY", "WEEKLY", "WEATHER DATA") // Tab titles
        val tabIcons = listOf(R.drawable.today, R.drawable.weekly_tab, R.drawable.weather_data_tab) // Icons for tabs
        // Set the adapter for ViewPager2
        mViewPager.adapter = MyViewPagerAdapter(this, currentLocation ?: "Los Angeles, CA", currentLatitude ?: "34", currentLogitude ?: "118", currentHumidity ?: "0", currentCloudCover ?: "0", currentPrecipitation ?: "0")
        // TabLayoutMediator to connect TabLayout and ViewPager2

        TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            val tabView: View = LayoutInflater.from(this).inflate(R.layout.tab_custom_layout, null)

            // Set the image and text
            val tabIcon = tabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)

            tabIcon.setImageResource(tabIcons[position])
            tabText.text = tabTitles[position]

            // Set the custom view to the tab
            tab.customView = tabView
        }.attach()
    }
}
