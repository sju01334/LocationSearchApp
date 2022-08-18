package com.nepplus.locationsearchapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.nepplus.locationsearchapp.adapter.SearchRecyclerAdapter
import com.nepplus.locationsearchapp.databinding.ActivityMainBinding
import com.nepplus.locationsearchapp.model.LocationLatLngEntity
import com.nepplus.locationsearchapp.model.SearchResultEntity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter : SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
        initData()
        setData()

    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() = with(binding){
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun initData(){
        adapter.notifyDataSetChanged()
    }

    private fun setData(){
        val dataList = (0..10).map{
            SearchResultEntity(
                name = "빌딩 $it",
                fullAdress = "주소 $it",
                locationLatLng = LocationLatLngEntity(
                    it.toFloat(),
                    it.toFloat()
                )
            )
        }
        adapter.setSearchResultListener(dataList){
            Toast.makeText(this, "빌딩이름 : ${it.name}", Toast.LENGTH_SHORT).show()
        }
    }


}



