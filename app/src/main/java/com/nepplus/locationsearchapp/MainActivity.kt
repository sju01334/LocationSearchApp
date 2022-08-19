package com.nepplus.locationsearchapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.nepplus.locationsearchapp.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.nepplus.locationsearchapp.adapter.SearchRecyclerAdapter
import com.nepplus.locationsearchapp.databinding.ActivityMainBinding
import com.nepplus.locationsearchapp.model.LocationLatLngEntity
import com.nepplus.locationsearchapp.model.SearchResultEntity
import com.nepplus.locationsearchapp.response.search.Poi
import com.nepplus.locationsearchapp.response.search.Pois
import com.nepplus.locationsearchapp.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job : Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main +job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter : SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        initData()
        bindViews()

    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() = with(binding){
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding){
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun initData(){
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois){
        val dataList = pois.poi.map{
            SearchResultEntity(
                name = it.name?: "빌딩명 없음",
                fullAdress = makeMainAdress(it),
                locationLatLng = LocationLatLngEntity(it.noorLat, it.noorLon)
            )
        }
        adapter.setSearchResultListener(dataList){
            Toast.makeText(this, "빌딩이름 : ${it.name}", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                }
            )
        }
    }

    private fun searchKeyword(keywordString: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO){
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if(response.isSuccessful){
                        val body = response.body()
                        withContext(Dispatchers.Main){
                            Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "검색안됨 : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }


}



