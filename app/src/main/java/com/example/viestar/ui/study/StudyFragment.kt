package com.example.viestar.ui.study

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.R
import com.example.viestar.ui.home.tab.local.Film
import com.example.viestar.ui.home.tab.local.ListFilmAdapter
import com.example.viestar.ui.profile.ProfileViewModel

class StudyFragment : Fragment() {

    private lateinit var rvWord: RecyclerView
    private val list = ArrayList<Word>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study, container, false)
        rvWord = view.findViewById(R.id.rv_word)
        rvWord.setHasFixedSize(true)

        list.addAll(getListWord())
        showRecyclerList()

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_local, container, false)
        return view
    }


    private fun getListWord(): ArrayList<Word> {
        val dataName = resources.getStringArray(R.array.data_word)
        val listWord = ArrayList<Word>()
        for (i in dataName.indices) {
            val word = Word(dataName[i])
            listWord.add(word)
        }
        return listWord
    }

    private fun showRecyclerList() {
        rvWord.layoutManager = GridLayoutManager(requireContext(), 3)
        val listWordAdapter = ListWordAdapter(list)
        rvWord.adapter = listWordAdapter
    }
}