# RealTimeWeatherApp

# Android Weather App

I developed a fully-featured **Android Weather Application** as part of CSCI 571 (Web Technologies).  
The app integrates a **Node.js backend**, **MongoDB Atlas**, and the **Tomorrow.io Weather API**, while following Google’s Material Design guidelines for UI.

---

## Features
- **Splash Screen & App Icon**  
  Custom app icon with attribution to Tomorrow.io, displayed on launch.
  
- **Home Screen / Current Location View**  
  - Automatically fetches current location (via IPinfo API).  
  - Displays temperature, condition summary, weather icons, and quick stats (humidity, windspeed, visibility, pressure).  
  - Includes a scrollable weekly forecast table.

- **City Search with Autocomplete**  
  - Google-style searchable interface with auto-suggestions.  
  - Progress bar shown while fetching results.  
  - Search results dynamically reuse the Home Screen layout.

- **Detailed Weather Information View**  
  - 3 tabs (`Today`, `Weekly`, `Weather Data`).  
  - **Today tab**: 9 styled weather metric cards.  
  - **Weekly tab**: Highcharts AreaRange chart for high/low temperatures.  
  - **Weather Data tab**: Highcharts graph for cloud cover, precipitation, and humidity.  
  - Built-in **Tweet (X) intent**: Share weather updates directly.

- **Favorite Cities**  
  - Add/remove cities with a Floating Action Button (pin + / pin –).  
  - Favorites persist across sessions using MongoDB Atlas.  
  - Dynamic tabs automatically update on add/remove actions.  

- **Error Handling & UX**  
  - Loading indicators (progress bars) for all async API calls.  
  - Proper error messages for missing/malformed data (no crashes).  
  - Rounded numbers, styled cards, and ripple effects for interactive UI.

---

## Tech Stack
- **Frontend:** Java (Android Studio, Android SDK, Material Design, Fragments, RecyclerViews)  
- **Backend:** Node.js , deployed on GCP
- **Database:** MongoDB Atlas
- **APIs:** Tomorrow.io, IPinfo API, Google Maps APIs
- **Libraries:**  
  - [Volley](https://developer.android.com/training/volley) for async HTTP requests  
  - [Highcharts Android](https://www.highcharts.com/blog/tutorials/highcharts-android-wrapper-tutorial/) for graphs  
  - [Picasso / Glide](https://square.github.io/picasso/) for image loading  

---
