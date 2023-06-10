package com.example.viestar

//import com.example.viestar.databinding.ActivityMainBinding
import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.viestar.databinding.ActivityMainBinding
import com.example.viestar.ui.home.DetailFilmFragment
import com.example.viestar.ui.home.HomeViewModel
import com.example.viestar.ui.home.MediaStoreVideo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import java.util.*

private const val DELETE_PERMISSION_REQUEST = 0x1033
private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tvText: TextView
    private lateinit var navigation: BottomNavigationView
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val galleryAdapter = GalleryAdapter { video ->
            deleteImage(video)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.text = searchView.text
                    searchView.hide()
                    galleryAdapter.filterData(searchView.text.toString())
                    Toast.makeText(this@MainActivity, searchView.text, Toast.LENGTH_SHORT).show()
                    false
                }
//            searchBar.inflateMenu(R.menu.option_menu)
//            searchBar.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.menu1 -> {
//                        true
//                    }
//                    R.id.menu2 -> {
//                        true
//                    }
//                    else -> false
//                }
//            }
        }


//        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFF")))

//        if (getSupportActionBar() != null) {
//            getSupportActionBar()?.hide();
//        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setItemIconTintList(null);
//        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_home, R.id.navigation_home, R.id.navigation_study, R.id.navigation_profile
//        ).build()
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)


//        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

        // Set BackgroundDrawable

        // Set BackgroundDrawable
//        actionBar!!.setBackgroundDrawable(colorDrawable)



        binding.rvFilm.also { view ->
            view.layoutManager = LinearLayoutManager(this)
            view.adapter = galleryAdapter
        }

        viewModel.videos.observe(this, Observer<List<MediaStoreVideo>> { videos ->
            galleryAdapter.submitList(videos)
            galleryAdapter.setData(videos)
        })

        galleryAdapter.filterData("")

//        galleryAdapter.setData()

        viewModel.permissionNeededForDelete.observe(this, Observer { intentSender ->
            intentSender?.let {
                startIntentSenderForResult(
                    intentSender,
                    com.example.viestar.DELETE_PERMISSION_REQUEST,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
        })

        openMediaStore()

//        binding.openAlbum.setOnClickListener { openMediaStore() }
        binding.grantPermissionButton.setOnClickListener { openMediaStore() }
        if (haveStoragePermission()) {
            showVideos()
//            binding.welcomeView.visibility = View.VISIBLE
        }

//        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.profile -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.textProfile, ProfileFragment())
//                        .addToBackStack(null)
//                        .commit()
//                    true
//                }
//                else -> false
//            }
//        }
        init()
        navigationListener()


//        setupSearchBar()
    }



    private fun searchListItems(query: String) {
        // Implementasikan logika pencarian pada ListAdapter sesuai dengan kebutuhan Anda
        // Misalnya, filter data berdasarkan query dan perbarui tampilan RecyclerView
        Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
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
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
//                    tvText.text = item.title
                    val intent = Intent(this, StudyActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0);
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            com.example.viestar.READ_EXTERNAL_STORAGE_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showVideos()
                    Log.d("Cekk", "wkwkwkwkwkwkk")
                } else {

                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )

                    if (showRationale) {
                        showNoAccess()
                    } else {
                        goToSettings()
                    }
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == com.example.viestar.DELETE_PERMISSION_REQUEST) {
            viewModel.deletePendingImage()
        }
    }

    private fun showVideos() {
        viewModel.loadVideos()
//        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.GONE
    }

    private fun showNoAccess() {
//        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.VISIBLE
    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            showVideos()
            Log.d("Cekkkkk", "wkwkwkwkwk")
        } else {
            requestPermission()
        }
    }

    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions,
                com.example.viestar.READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun deleteImage(image: MediaStoreVideo) {
        val detailFragment = DetailFilmFragment()
        val bundle = Bundle()
        bundle.putParcelable(DetailFilmFragment.EXTRA_DETAIL_FILM, image.contentUri)
        detailFragment.arguments = bundle

        val intent = Intent(this, DetailFilmActivity::class.java)
        intent.putExtra(DetailFilmActivity.EXTRA_DETAIL_FILM, image.contentUri)
        startActivity(intent)

//        fragmentManager?.beginTransaction()?.apply {
//            replace(R.id.nav_host_fragment, detailFragment, DetailFilmFragment::class.java.simpleName)
//            addToBackStack(null)
//            commit()
//        }

    }

    private inner class GalleryAdapter(val onClick: (MediaStoreVideo) -> Unit) :
        ListAdapter<MediaStoreVideo, VideoViewHolder>(MediaStoreVideo.DiffCallback) {
//        private val filteredData: MutableList<MediaStoreVideo> = mutableListOf()
        private var originalData: List<MediaStoreVideo> = emptyList()
        private var filteredData: List<MediaStoreVideo> = emptyList()

        fun setData(data: List<MediaStoreVideo>) {
            originalData = data
            filterData("")
        }

        fun filterData(searchQuery: String) {
            filteredData = if (searchQuery.isNotBlank()) {
                originalData.filter { item ->
                    item.displayName.contains(searchQuery, ignoreCase = true)
                }
            } else {
                originalData
            }
            submitList(filteredData)
        }

        // ...

        override fun getItemCount(): Int {
            return filteredData.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_film, parent, false)
            return VideoViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
//            val mediaStoreImage = getItem(position)
            val mediaStoreImage = filteredData[position]
            holder.rootView.tag = mediaStoreImage
            holder.tvName.text = mediaStoreImage.displayName

            Log.d("nama file", mediaStoreImage.contentUri.toString())

            Glide.with(holder.imageView)
                .load(mediaStoreImage.contentUri)
                .thumbnail(0.33f)
                .transform(RoundedCorners(30))
                .into(holder.imageView)
        }
    }

    private class VideoViewHolder(view: View, onClick: (MediaStoreVideo) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val rootView = view
        val imageView: ImageView = view.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        init {
            imageView.setOnClickListener {
                val image = rootView.tag as? MediaStoreVideo ?: return@setOnClickListener
                onClick(image)
            }
        }
    }
}