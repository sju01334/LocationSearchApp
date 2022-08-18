package com.nepplus.locationsearchapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nepplus.locationsearchapp.databinding.ItemSearchBinding
import com.nepplus.locationsearchapp.model.SearchResultEntity

class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>() {

    private var  searchResultList : List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener : (SearchResultEntity) -> Unit

    inner class SearchViewHolder(private val binding : ItemSearchBinding, val searchResultClickListener: (SearchResultEntity) -> Unit) : RecyclerView.ViewHolder(binding.root){
        fun bindData(data : SearchResultEntity) = with(binding){
            textTextView.text = data.name
            subtextTextView.text = data.fullAdress
        }

        fun bindViews(data:SearchResultEntity){
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(view, searchResultClickListener)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultListener(searchResultList: List<SearchResultEntity>, searchResultClickListener: (SearchResultEntity) ->Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}