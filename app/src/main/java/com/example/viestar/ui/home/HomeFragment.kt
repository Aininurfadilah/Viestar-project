package com.example.viestar.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.viestar.R
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.viestar.databinding.FragmentHomeBinding
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import com.example.viestar.ui.home.DetailFilmFragment
import com.example.viestar.ui.home.tab.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val DELETE_PERMISSION_REQUEST = 0x1033
private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

class HomeFragment : Fragment() {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

//    private lateinit var homeViewModel: HomeViewModel
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val galleryAdapter = GalleryAdapter { video ->
            deleteImage(video)
        }

        binding.rvFilm.also { view ->
            view.layoutManager = LinearLayoutManager(requireActivity())
            view.adapter = galleryAdapter
        }

        viewModel.videos.observe(viewLifecycleOwner, Observer<List<MediaStoreVideo>> { videos ->
            galleryAdapter.submitList(videos)
        })

        viewModel.permissionNeededForDelete.observe(viewLifecycleOwner, Observer { intentSender ->
            intentSender?.let {
                startIntentSenderForResult(
                    intentSender,
                    DELETE_PERMISSION_REQUEST,
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
//        binding.grantPermissionButton.setOnClickListener { openMediaStore() }
        if (haveStoragePermission()) {
            showVideos()
//            binding.welcomeView.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
//                    showImages()
                    Log.d("Cekk", "wkwkwkwkwkwkk")
                } else {

                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
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
        if (resultCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
            viewModel.deletePendingImage()
        }
    }

    private fun showVideos() {
        viewModel.loadVideos()
//        binding.welcomeView.visibility = View.GONE
//        binding.permissionRationaleView.visibility = View.GONE
    }

    private fun showNoAccess() {
//        binding.welcomeView.visibility = View.GONE
//        binding.permissionRationaleView.visibility = View.VISIBLE
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
        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${activity?.packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun deleteImage(image: MediaStoreVideo) {
        val detailFragment = DetailFilmFragment()
        val bundle = Bundle()
        bundle.putParcelable(DetailFilmFragment.EXTRA_DETAIL_FILM, image.contentUri)
        detailFragment.arguments = bundle

        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.nav_host_fragment, detailFragment, DetailFilmFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }

    }

    private inner class GalleryAdapter(val onClick: (MediaStoreVideo) -> Unit) :
        ListAdapter<MediaStoreVideo, VideoViewHolder>(MediaStoreVideo.DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_film, parent, false)
            return VideoViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
            val mediaStoreImage = getItem(position)
            holder.rootView.tag = mediaStoreImage
            holder.tvName.text = mediaStoreImage.displayName

            Log.d("nama file", mediaStoreImage.contentUri.toString())

            Glide.with(holder.imageView)
                .load(mediaStoreImage.contentUri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.imageView)
        }
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