package com.example.finalassign4

interface FavoritessCityProvider {
    fun getLatLong(position: Int): String?
    fun getCount(): Int
}