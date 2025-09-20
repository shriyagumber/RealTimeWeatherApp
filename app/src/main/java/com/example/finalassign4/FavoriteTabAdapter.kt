package com.example.finalassign4

import android.provider.Settings.Global.getString
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.finalassign4.FavoritessCityProvider

class FavoriteTabAdapter(fragmentActivity: FragmentActivity, favoritesCityProvider: FavoritessCityProvider): FragmentStateAdapter(fragmentActivity) {
    private lateinit var favoritesCityProvider: FavoritessCityProvider

    init {
        this.favoritesCityProvider = favoritesCityProvider
    }

    override fun getItemCount(): Int {
        return this.favoritesCityProvider.getCount()
    }

    override fun createFragment(position: Int): Fragment {
        val latLong: String = favoritesCityProvider.getLatLong(position).toString()
        var lat = ""
        var long = ""
        var locationStr = ""
        if (latLong != ""){
            Log.d("LATLONG", latLong)
            val latLongArr = latLong.split(',')
            lat = latLongArr[0]
            long = latLongArr[1]
            locationStr = latLongArr[2]
        }

        return  OverallFragment.newInstance(lat, long, locationStr)
    }
}
