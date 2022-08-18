package com.nepplus.locationsearchapp

import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nepplus.locationsearchapp.databinding.ActivityMapBinding
import com.nepplus.locationsearchapp.model.SearchResultEntity

class MapActivity: AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY = "SearchResult"
        const val PERMISSION_REQUEST_CODE = 1
        const val CAMERA_ZOOM_LEVEL = 17f
    }

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private lateinit var searchResult: SearchResultEntity
    private  var currentSelectMarker : Marker? = null
    private lateinit var locationManager: LocationManager
//    private lateinit var myLocationListener: MyLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!::searchResult.isInitialized){
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY) ?: throw Exception("데이터가 유효하지 않습니다.")
                setupGoogleMap()
            }
        }


    }

    private fun setupGoogleMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentSelectMarker = setupMarker(searchResult)

        currentSelectMarker?.showInfoWindow()
    }

    private fun setupMarker(searchResult : SearchResultEntity) : Marker? {
        val positionLatLng = LatLng(searchResult.locationLatLng.latitude.toDouble(), searchResult.locationLatLng.longitude.toDouble())
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAdress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }


}