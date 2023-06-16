package com.example.viestar

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viestar.databinding.ActivityStudyBinding
import com.example.viestar.ui.study.ListWordAdapter
import com.example.viestar.ui.study.Word
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var rvWord: RecyclerView
    private lateinit var navigation: BottomNavigationView
    private val list = ArrayList<Word>()
//    private lateinit var listWordAdapter: ListWordAdapter

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

//        callAPI("dogs")

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
        val listWordAdapter = ListWordAdapter(list, this)
        rvWord.adapter = listWordAdapter

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.text = searchView.text
                    searchView.hide()
                    listWordAdapter.filterList(searchView.text.toString())
//                    Toast.makeText(this@StudyActivity, searchView.text, Toast.LENGTH_SHORT).show()
                    false
                }
        }

//        val adapter = ListWordAdapter(list, context)
        listWordAdapter.setOnItemClickListener(object : ListWordAdapter.OnItemClickListener {
            override fun onItemClick(word: Word) {
                // Tampilkan pop-up atau lakukan tindakan lain sesuai kebutuhan
//                showPopup(word.name)
                callAPI(word.name)
//                callAPI(word.name)
            }

            var namaWord: String = ""
            private var isLoading = false

            private var progressDialog: ProgressDialog? = null

            private fun showLoadingIndicator() {
                progressDialog = ProgressDialog.show(this@StudyActivity, null, "Loading...", true)
            }

            private fun hideLoadingIndicator() {
                progressDialog?.dismiss()
                progressDialog = null
            }


            private fun callAPI(itemName: String) {
                isLoading = true
                showLoadingIndicator()
                val apiService = ApiConfig.getApiService()

                val call = apiService.searchWordName(itemName)
                call.enqueue(object : Callback<List<SearchWordResponse>> {
                    override fun onResponse(
                        call: Call<List<SearchWordResponse>>,
                        response: Response<List<SearchWordResponse>>
                    ) {
                        isLoading = false
                        hideLoadingIndicator()
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            Log.d("search", response.toString())
                            namaWord = ""
                           responseData?.forEach { word ->
                                val wordName = word.word
                                val wordScore = word.score
                                val wordTags = word.tags
//                                Log.d("looping", wordName.toString())
                               namaWord += wordName + ", "
                            }

                            showPopup(namaWord, itemName)
                            Log.d("cek data", namaWord)
//                            showPopup(looping)
                            // Proses responseData sesuai kebutuhan
                        } else {
                            Log.d("tes api error", "aning api error")
                            // Tangani respons error
                        }
                    }

                    override fun onFailure(call: Call<List<SearchWordResponse>>, t: Throwable) {
                        // Tangani kegagalan pemanggilan API
                        hideLoadingIndicator()
                    }
                })
            }

            fun showPopup(listWord: String,namaWord: String ) {

                MaterialAlertDialogBuilder(this@StudyActivity)
                    .setTitle(namaWord)
                    .setMessage(listWord)
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to neutral button press
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
            }

        })



    }


}