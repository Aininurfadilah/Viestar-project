package com.example.viestar.ui.study

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.R
import com.example.viestar.ui.home.tab.local.Film
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import java.util.*
import kotlin.collections.ArrayList
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class ListWordAdapter(private val listWord: ArrayList<Word>, private val context: Context) : RecyclerView.Adapter<ListWordAdapter.ListViewHolder>() {

    private val originalList: ArrayList<Word> = ArrayList(listWord)
    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun filterList(query: String) {
        val filteredList = ArrayList<Word>()

        if (query.isNotBlank()) {
            val searchQuery = query.toLowerCase(Locale.getDefault())
            for (word in originalList) {
                if (word.name.toLowerCase(Locale.getDefault()).contains(searchQuery)) {
                    filteredList.add(word)
                }
            }
        } else {
            filteredList.addAll(originalList)
        }

        listWord.clear()
        listWord.addAll(filteredList)
        notifyDataSetChanged()

        if (filteredList.isEmpty()) {
            // Menampilkan toast jika hasil pencarian kosong
            Toast.makeText(context, "Hasil pencarian tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(word: Word)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name) = listWord[position]
        holder.tvWord.text = name
        holder.tvWord.setOnClickListener {
            val word = listWord[position]
            itemClickListener?.onItemClick(word)
        }

    }

    override fun getItemCount(): Int = listWord.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tv_word)
    }
}