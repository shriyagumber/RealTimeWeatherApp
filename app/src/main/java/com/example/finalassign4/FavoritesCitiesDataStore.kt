import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Extension property to initialize DataStore
val Context.dataStore by preferencesDataStore(name = "favorite_cities_store")

class FavoriteCitiesDataStore(private val context: Context) {

    // Key to store the set of favorite cities
    private val FAVORITE_CITIES_KEY = stringSetPreferencesKey("favorite_cities_key")

    // Retrieve favorite cities as a Flow
    val favoriteCitiesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_CITIES_KEY] ?: emptySet()
        }

    // Add a city to favorites
    suspend fun addCityToFavorites(cityName: String) {
        context.dataStore.edit { preferences ->
            val currentCities = preferences[FAVORITE_CITIES_KEY] ?: emptySet()
            preferences[FAVORITE_CITIES_KEY] = currentCities + cityName
        }
    }

    // Remove a city from favorites
    suspend fun removeCityFromFavorites(cityName: String) {
        context.dataStore.edit { preferences ->
            val currentCities = preferences[FAVORITE_CITIES_KEY] ?: emptySet()
            preferences[FAVORITE_CITIES_KEY] = currentCities - cityName
        }
    }

    // Get current favorite cities as a Set<String>
    suspend fun getCurrentFavoriteCities(): Set<String> {
        val preferences = context.dataStore.data.map { it }.first()
        return preferences[FAVORITE_CITIES_KEY] ?: emptySet()
    }
}