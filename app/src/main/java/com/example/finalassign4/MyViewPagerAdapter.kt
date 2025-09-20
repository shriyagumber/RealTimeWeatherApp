package com.example.finalassign4

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyViewPagerAdapter(fragmentActivity: FragmentActivity, private val currentLocation: String?, private val currentLatitude: String?, private val currentLongitude: String?, private val currentHumidity: String?, private val currentCloudCover: String?, private val currentPrecipitation: String? ): FragmentStateAdapter(fragmentActivity,
    ){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment
        val bundle = Bundle()
        bundle.putString("currentLocation", currentLocation)
        bundle.putString("currentLatitude", currentLatitude)
        bundle.putString("currentLongitude", currentLongitude)
        bundle.putString("currentHumidity", currentHumidity)
        bundle.putString("currentCloudCover", currentCloudCover)
        bundle.putString("currentPrecipitation", currentPrecipitation)
        when(position) {
            0 -> {
                fragment = Fragment1()
                fragment.arguments = bundle
            }
            1 -> {
                fragment = Fragment2()
                fragment.arguments = bundle
            }
            2 -> {
                fragment = Fragment3()
                fragment.arguments = bundle
            }
            else -> throw IllegalStateException("Unexpected position $position")
        }
        return  fragment
    }

}