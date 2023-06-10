package com.example.viestar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.databinding.ActivityMainBinding
import com.example.viestar.databinding.ActivityStudyBinding
import com.example.viestar.ui.study.ListWordAdapter
import com.example.viestar.ui.study.Word
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var rvWord: RecyclerView
    private lateinit var navigation: BottomNavigationView
    private val list = ArrayList<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setItemIconTintList(null);

        // Set Home selected
        navView.setSelectedItemId(R.id.navigation_study);

        rvWord = findViewById(R.id.rv_word)
        rvWord.setHasFixedSize(true)

        list.addAll(getListWord())
        showRecyclerList()

        init()
        navigationListener()
    }

    private fun init() {
//        tvText = findViewById(R.id.tv_text)
        navigation = findViewById(R.id.nav_view)
    }

    private fun navigationListener() {
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
//                    tvText.text = item.title
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0);
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
//                    tvText.text = item.title
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
//                    tvText.text = item.title
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
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
        rvWord.layoutManager = GridLayoutManager(this, 3)
        val listWordAdapter = ListWordAdapter(list)
        rvWord.adapter = listWordAdapter
    }


}