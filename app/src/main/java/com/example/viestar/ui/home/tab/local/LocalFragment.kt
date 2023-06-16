package com.example.viestar.ui.home.tab.local

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.R

class LocalFragment : Fragment() {

    private lateinit var rvFilm: RecyclerView
    private val list = ArrayList<Film>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_local, container, false)
        rvFilm = view.findViewById(R.id.rv_film)
        rvFilm.setHasFixedSize(true)

        list.addAll(getListFilm())
        showRecyclerList()

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_local, container, false)
        return view
    }


    private fun getListFilm(): ArrayList<Film> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listFilm = ArrayList<Film>()
        for (i in dataName.indices) {
            val film = Film(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listFilm.add(film)
        }
        return listFilm
    }

    private fun showRecyclerList() {
        rvFilm.layoutManager = LinearLayoutManager(requireContext())
        val listFilmAdapter = ListFilmAdapter(list)
        rvFilm.adapter = listFilmAdapter
    }

}