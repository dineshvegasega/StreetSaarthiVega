package com.ezatpanah.hilt_retrofit_paging_youtube.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vegasega.streetsaarthi.databinding.ItemMoviesBinding
import com.vegasega.streetsaarthi.models.ItemLiveScheme
import javax.inject.Inject

class MoviesAdapter @Inject() constructor() :
    PagingDataAdapter<ItemLiveScheme, MoviesAdapter.ViewHolder>(differCallback) {

    private lateinit var binding: ItemMoviesBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemMoviesBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: ItemLiveScheme) {
            binding.apply {
                tvMovieName.text = item.name
                tvMovieDateRelease.text = item.start_at
//                tvRate.text=item.voteAverage.toString()
//                val moviePosterURL = POSTER_BASE_URL + item?.posterPath
//                imgMovie.load(moviePosterURL){
//                    crossfade(true)
//                    placeholder(R.drawable.poster_placeholder)
//                    scale(Scale.FILL)
//                }
//                tvLang.text=item.originalLanguage
//
//                root.setOnClickListener {
//                    onItemClickListener?.let {
//                        it(item)
//                    }
//                }
            }
        }
    }

    private var onItemClickListener: ((ItemLiveScheme) -> Unit)? = null

    fun setOnItemClickListener(listener: (ItemLiveScheme) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<ItemLiveScheme>() {
            override fun areItemsTheSame(oldItem: ItemLiveScheme, newItem: ItemLiveScheme): Boolean {
                return oldItem.scheme_id == newItem.scheme_id
            }

            override fun areContentsTheSame(oldItem: ItemLiveScheme, newItem: ItemLiveScheme): Boolean {
                return oldItem == newItem
            }
        }
    }

}