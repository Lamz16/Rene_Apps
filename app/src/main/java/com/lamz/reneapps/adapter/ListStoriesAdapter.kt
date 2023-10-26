package com.lamz.reneapps.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lamz.reneapps.databinding.ItemStoryBinding
import com.lamz.reneapps.response.ListStoryItem
import com.lamz.reneapps.ui.detail.DetailActivity
import com.lamz.reneapps.ui.maps.MapsActivity


class ListStoriesAdapter : PagingDataAdapter<ListStoryItem, ListStoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStory : ListStoryItem) {
            Glide.with(itemView)
                .load(listStory.photoUrl)
                .into(binding.imgStory)
            binding.tvAuthor.text = listStory.name
            binding.tvDescription.text = listStory.description

            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.EXTRA_ID, listStory.id )
                itemView.context.startActivity(intentDetail , ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }

            binding.ivLocation.setOnClickListener {
                val intent = Intent(itemView.context, MapsActivity::class.java)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}