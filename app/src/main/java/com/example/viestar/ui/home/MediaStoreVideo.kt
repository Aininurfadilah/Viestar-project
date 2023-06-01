package com.example.viestar.ui.home

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class MediaStoreVideo(
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaStoreVideo>() {
            override fun areItemsTheSame(oldItem: MediaStoreVideo, newItem: MediaStoreVideo) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaStoreVideo, newItem: MediaStoreVideo) =
                oldItem == newItem
        }
    }
}