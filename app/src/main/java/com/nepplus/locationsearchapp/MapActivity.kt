package com.nepplus.locationsearchapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nepplus.locationsearchapp.databinding.ActivityMapBinding
import com.nepplus.locationsearchapp.model.LocationLatLngEntity
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
    private lateinit var myLocationListener: MyLocationListener

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

        bindViews()
    }

    private fun bindViews() = with(binding) {
        currentLocationButton.setOnClickListener {
            getMyLocation()
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

    private fun getMyLocation() {
        //초기화가 안되었으면
        if (::locationManager.isInitialized.not()) {
            //초기화 해줘!
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        //GPS 가능여부 체크해줘!
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        //GPS 허락 받았으면
        if (isGpsEnable) {
            //권한이 둘중에 하나라도 없으면
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                setMyLocationListener()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private  fun setMyLocationListener(){
        //최소시간 설정
        val minTime = 1500L
        //최소 허용 거리단위
        val minDistance = 100f

        if(::myLocationListener.isInitialized.not()){
            myLocationListener = MyLocationListener()
        }

        with(locationManager){
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(
                locationLatLngEntity.latitude.toDouble(),
                locationLatLngEntity.longitude.toDouble()
            )
        , CAMERA_ZOOM_LEVEL))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                setMyLocationListener()
            }else{
                Toast.makeText(this, "권한을 받지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MyLocationListener: LocationListener{
        override fun onLocationChanged(location: Location) {
            val locationLatLngEntity = LocationLatLngEntity(location.latitude.toFloat(), location.longitude.toFloat())
            onCurrentLocationChanged(locationLatLngEntity)
        }

    }


}