package com.example.emotionbasedmusic.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.data.Music
import com.example.emotionbasedmusic.databinding.SlideSongItemViewBinding
import com.squareup.picasso.Picasso

class SongImageAdapter(private val context: Context): RecyclerView.Adapter<SongImageAdapter.SongImageViewHolder>() {

    private var list = listOf<String>()
    private var intList = listOf<Int>()
    private var listSize = 8
    @SuppressLint("NotifyDataSetChanged")
    fun bindList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun bindIntList(list: List<Int>) {
        this.intList = list
        notifyDataSetChanged()
    }
    inner class SongImageViewHolder(binding: SlideSongItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongImageViewHolder {
        return SongImageViewHolder(SlideSongItemViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: SongImageViewHolder, position: Int) {
        val imgUrl = list[position]
        Picasso.get().load(imgUrl).placeholder(R.drawable.untitled_141).into(holder.img)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}