package com.example.viestar.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.R

class StudyFragment : Fragment() {

    private lateinit var rvWord: RecyclerView
    private val list = ArrayList<Word>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study, container, false)
        rvWord = view.findViewById(R.id.rv_word)
        rvWord.setHasFixedSize(true)

        list.addAll(getListWord())

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

}