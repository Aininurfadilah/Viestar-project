package com.example.viestar.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.R
import com.example.viestar.ui.home.tab.local.Film

class ListWordAdapter(private val listWord: ArrayList<Word>) : RecyclerView.Adapter<ListWordAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name) = listWord[position]
        holder.tvWord.text = name
    }

    override fun getItemCount(): Int = listWord.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tv_word)
    }
}