package com.example.finalassign4

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.finalassign4.FavoriteTabAdapter
import com.example.finalassign4.FavoritessCityProvider
import FavoriteCitiesDataStore
import android.R.attr.dialogIcon
import android.R.attr.launchTaskBehindTargetAnimation
import android.annotation.SuppressLint
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dataStore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),FavoritessCityProvider {
//    private val BACKEND_URL = "https://csci571assn3.ue.r.appspot.com/"
    private val BACKEND_URL = "http://10.0.0.202:3000"
    private val GEOCODING_API_KEY = "AIzaSyA8PLBJrghiHuMpMjhFQ3LVgTuuGZ10m0k"
    private val GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json"

    private var latitude: String = ""
    private var longitude: String = ""
    private var currentLocation: String = ""

    private lateinit var autoCompleteInput: AutoCompleteTextView
    private lateinit var requestQueue: RequestQueue

    private lateinit var fab: FloatingActionButton
    private lateinit var viewPager: ViewPager2
    private lateinit var favoritesDataStore: FavoriteCitiesDataStore
    private lateinit var favoritesAdapter: FavoriteTabAdapter
    private var favorites = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
        autoCompleteInput = findViewById(R.id.userInput)
        fab = findViewById(R.id.fab)
        viewPager = findViewById(R.id.favorites_pager)
        favoritesDataStore = FavoriteCitiesDataStore(this)
        this.favoritesAdapter = FavoriteTabAdapter(this, this)
        viewPager.setAdapter(favoritesAdapter)
        requestQueue = Volley.newRequestQueue(this)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        findViewById<ImageView>(R.id.search_button).setOnClickListener(View.OnClickListener { // Enable the EditText when the button is clicked
            autoCompleteInput.visibility = View.VISIBLE
        })

        setupAutocomplete()
        favorites.clear()



        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")){
            latitude = intent.getStringExtra("latitude")!!
            longitude = intent.getStringExtra("longitude")!!
            currentLocation = intent.getStringExtra("currentLocation")!!
            favorites.add("${latitude},${longitude},${currentLocation}") // first screen is current location
            Log.d("MainActivity", "When there is intent ${latitude},${longitude},${currentLocation}")
            for (favorite in favorites) {

                Log.d("MainActivity", "Favorite: $favorite")
            }
            fab.setImageDrawable(getDrawable(R.drawable.rem_fav))
            Log.d("In if MainActivity", "When there is intent")

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = ""
            }.attach()
//            check if current page is already in favorites
            tabLayout.visibility = View.GONE
        }
        else {
            favorites.add("") // first screen is current location
            fab.visibility = View.GONE
            fab.setImageDrawable(getDrawable(R.drawable.add_fav))
            Log.d("MainActivity", "When there is no intent")

            lifecycleScope.launch {
                val allFavorites = favoritesDataStore.getCurrentFavoriteCities()
                for (favorite in allFavorites) {
                    Log.d("MainActivity", "Favorite: $favorite")
                    favorites.add(favorite)
                }
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = ""
                }.attach()
            }
//            updateFabIcon()
        }

        fab.setOnClickListener(
            View.OnClickListener {
                lifecycleScope.launch {
                    toggleFavorite()
//                    if (latitude == "") {
//                        favoritesDataStore.addCityToFavorites("${latitude},${longitude},${currentLocation}")
//                    }
//                    else {
//                        favoritesDataStore.removeCityFromFavorites("${latitude},${longitude},${currentLocation}")
//                    }
                }

            }
        )
    }

    private suspend fun toggleFavorite() {
        val cityKey = "$latitude,$longitude,$currentLocation"
        if (latitude.isEmpty() || longitude.isEmpty() || currentLocation.isEmpty()) {
            Toast.makeText(this, "Invalid city details", Toast.LENGTH_SHORT).show()
            return
        }

        if (isCityInFavorites(cityKey)) {
            favoritesDataStore.removeCityFromFavorites(cityKey)
            favorites.remove(cityKey)
            fab.setImageDrawable(getDrawable(R.drawable.add_fav))
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoritesDataStore.addCityToFavorites(cityKey)
            favorites.add(cityKey)
            fab.setImageDrawable(getDrawable(R.drawable.rem_fav))
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        favoritesAdapter.notifyDataSetChanged()
    }



    private suspend fun isCityInFavorites(cityKey: String): Boolean {
        val targetLocation = cityKey.split(",")[2].trim()

        val allFavorites = favoritesDataStore.getCurrentFavoriteCities()
        return allFavorites.any { favorite ->
            val arr = favorite.split(",")
            if (arr.size >= 3) {
                Log.d("MainActivity", "Checking: Target Location '$targetLocation' against '${arr[2].trim()}'")
                arr[2].trim().equals(targetLocation, ignoreCase = true)
            } else {
                Log.d("MainActivity", "Skipping comparison, insufficient data: $favorite")
                false
            }
        }
    }




    override fun onRestart() {
        super.onRestart()

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        favorites.clear()

        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")){
            latitude = intent.getStringExtra("latitude")!!
            longitude = intent.getStringExtra("longitude")!!
            currentLocation = intent.getStringExtra("currentLocation")!!
            favorites.add("${latitude},${longitude},${currentLocation}") // first screen is current location
//            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//                tab.text = favorites[position]
//            }.attach()
//            check if current page is already in favorites
            tabLayout.visibility = View.GONE
        }
        else {
            favorites.add("") // first screen is current location
            fab.visibility = View.GONE

            lifecycleScope.launch {
                val allFavorites = favoritesDataStore.getCurrentFavoriteCities()
                for (favorite in allFavorites) {
                    Log.d("MainActivity", "Favorite: $favorite")
                    favorites.add(favorite)
                }
//                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//                    tab.text = ""
//                }.attach()
            }

        }
        favoritesAdapter.notifyDataSetChanged();
    }

    private fun setupAutocomplete() {
        autoCompleteInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank() || s.length < 2) return // Start autocomplete after 3 characters
                fetchCitySuggestions(s.toString())
            }
        })
        autoCompleteInput.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            fetchLatLongFromGeocode(selectedItem)
            autoCompleteInput.visibility = View.VISIBLE
        }
    }

    private fun fetchCitySuggestions(input: String) {
        val url = "${BACKEND_URL}/city_autocomplete?input=$input"

        // Fetch autocomplete suggestions
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val suggestions = mutableListOf<String>()
                    for (i in 0 until response.length()) {
                        val obj = response.getJSONObject(i)
                        val city = obj.optString("city", "Unknown City")
                        val state = obj.optString("state", "Unknown State")
                        suggestions.add("$city, $state")
                    }
                    // Update AutoCompleteTextView adapter
                    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
                    autoCompleteInput.setAdapter(adapter)
                    autoCompleteInput.showDropDown()

                } catch (e: Exception) {
                    Log.e("Autocomplete", "Error parsing suggestions: ${e.message}")
                    Toast.makeText(this, "Error fetching suggestions", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Autocomplete", "Error fetching suggestions: ${error.message}")
                Toast.makeText(this, "Failed to fetch suggestions", Toast.LENGTH_SHORT).show()
            }
        )

        // Add request to queue
        requestQueue.add(jsonArrayRequest)
    }
    private fun fetchLatLongFromGeocode(address: String) {
        val encodedAddress = java.net.URLEncoder.encode(address, "UTF-8")
        val url = "${GEOCODING_URL}?address=$encodedAddress&key=$GEOCODING_API_KEY"

        val rootView: View = findViewById(R.id.root_layout)
        val progressOverlay: View = findViewById(R.id.progress_overlay)

        // Show progress overlay and hide root layout
        progressOverlay.visibility = View.VISIBLE
        rootView.visibility = View.GONE

        val geocodeRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressOverlay.visibility = View.GONE
                rootView.visibility = View.VISIBLE

                try {
                    val results = response.getJSONArray("results")
                    if (results.length() > 0) {
                        val location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                        val latitude = location.getDouble("lat").toString()
                        val longitude = location.getDouble("lng").toString()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("latitude", latitude)
                        intent.putExtra("longitude", longitude)
                        intent.putExtra("currentLocation", address)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No location found for the input", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error fetching location details", Toast.LENGTH_SHORT).show()
                    Log.e("Geocode", "Error parsing response: ${e.message}")
                }
            },
            { error ->
                progressOverlay.visibility = View.GONE
                rootView.visibility = View.VISIBLE
                Toast.makeText(this, "Failed to fetch location details", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(geocodeRequest)
    }


    override fun getLatLong(position: Int): String {
        return favorites[position]
    }

    override fun getCount(): Int {
        return favorites.size
    }
}
