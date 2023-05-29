package com.example.viestar.ui.home.tab.local

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viestar.R

class LocalFragment : Fragment() {

    private lateinit var rvFilm: RecyclerView
    private val list = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rvHeroes = findViewById(R.id.rv_film)
        rvHeroes.setHasFixedSize(true)

        list.addAll(getListFilm())
        showRecyclerList()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local, container, false)
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
        rvFilm.layoutManager = LinearLayoutManager(this)
        val listFilmAdapter = ListFilmAdapter(list)
        rvFilm.adapter = listFilmAdapter
    }

}