package com.example.viestar

import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import com.example.viestar.databinding.ActivityDetailFilmBinding
import com.example.viestar.databinding.MoreFeaturesBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.ImmutableList
import java.io.File
import java.util.*


class DetailFilmActivity : AppCompatActivity() {

//    private lateinit var detailBinding : ActivityDetailBinding

    private val detailBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailFilmBinding.inflate(layoutInflater)
    }

    var properties: DialogProperties = DialogProperties()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail)
//        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        supportActionBar?.title = "Detail "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val data = intent.getParcelableExtra<Film>(EXTRA_DETAIL_FILM)
        val data = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)
        Log.d("Tes", data.toString())

//        with(detailBinding){
//            name.text = data.toString()
//
//        }


        setMoreButton()


    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val dialog = FilePickerDialog(this@DetailFilmActivity, properties)
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show()
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(
                        this@DetailFilmActivity,
                        "Permission is Required for getting list of files",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private var player: ExoPlayer? = null

    private fun initializePlayer() {
        val data = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)
        val mediaItem = MediaItem.fromUri(data.toString())
//        val anotherMediaItem = MediaItem.fromUri(URL_AUDIO)
        trackSelector = DefaultTrackSelector(this)
        player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build().also { exoPlayer ->
            detailBinding.videoView.player = exoPlayer
            exoPlayer.setMediaItem(mediaItem)
//            exoPlayer.addMediaItem(anotherMediaItem)
            exoPlayer.prepare()
        }
    }

    private fun stopAndReleasePlayer() {
        player?.release()
    }

    private fun initializePlayerSubtitle(subtitleUrl: String) {
        stopAndReleasePlayer()
        val videoUrl = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)

        val subtitle = MediaItem.Subtitle(
            Uri.parse(subtitleUrl),
            MimeTypes.APPLICATION_SUBRIP,
            "en",
            C.SELECTION_FLAG_DEFAULT
        )

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setSubtitles(listOf(subtitle))
            .build()

        val renderersFactory = DefaultRenderersFactory(this)
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

        val trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .build().also { exoPlayer ->
                detailBinding.videoView.player = exoPlayer
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
    }


//    private fun initializePlayerSubtitle(subtitleUrl: String) {
//        val data = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)
//
//        val subtitle = MediaItem.SubtitleConfiguration.Builder(subtitleUrl)
//            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
//            .setLanguage("en")
//            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
//            .build()
//        val mediaItem = MediaItem.fromUri(data.toString()).setSubtitleConfigurations(ImmutableList.of(subtitle)).build()
//        trackSelector = DefaultTrackSelector(this)
//        player = ExoPlayer.Builder(this)
//            .setTrackSelector(trackSelector)
//            .build().also { exoPlayer ->
//                detailBinding.videoView.player = exoPlayer
//                exoPlayer.setMediaItem(mediaItem)
//                exoPlayer.prepare()
//        }
//    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        if (Util.SDK_INT <= 23 && player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, detailBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setMoreButton() {
        findViewById<ImageButton>(R.id.moreFeaturesBtn).setOnClickListener {
            player?.pause()
            val customDialog = LayoutInflater.from(this).inflate(R.layout.more_features, detailBinding.root, false)
            val bindingMF = MoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                .setOnCancelListener { player?.play() }
                .setBackground(ColorDrawable(0x803700B3.toInt()))
                .create()
            dialog.show()

            bindingMF.showSubtitle.setOnClickListener {
                val properties = DialogProperties()
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.extensions = arrayOf(".srt")
                properties.root = File(DialogConfigs.DEFAULT_DIR)

                val dialog = FilePickerDialog(this@DetailFilmActivity, properties)
                dialog.setTitle("Select a File")
                dialog.setProperties(properties)

                dialog.setDialogSelectionListener { files ->
                    // `files` adalah array yang berisi path file yang dipilih oleh pengguna
                    if (files.isNotEmpty()) {
                        val selectedFile = File(files[0])
                        // Lakukan sesuatu dengan file yang dipilih
                        // Ambil path file SRT yang dipilih
                        initializePlayerSubtitle(selectedFile.toString())

                    }
                }

                dialog.show()
            }

            bindingMF.audioTrack.setOnClickListener {
                dialog.dismiss()
                player?.play()
                val audioTrack = ArrayList<String>()
                val audioList = ArrayList<String>()
                for(group in player!!.currentTracksInfo.trackGroupInfos){
                    if(group.trackType == C.TRACK_TYPE_AUDIO){
                        val groupInfo = group.trackGroup
                        for (i in 0 until groupInfo.length){
                            audioTrack.add(groupInfo.getFormat(i).language.toString())
                            audioList.add("${audioList.size + 1}. " + Locale(groupInfo.getFormat(i).language.toString()).displayLanguage
                                    + " (${groupInfo.getFormat(i).label})")
                        }
                    }
                }

                if(audioList[0].contains("null")) audioList[0] = "1. Default Track"

                val tempTracks = audioList.toArray(arrayOfNulls<CharSequence>(audioList.size))
                val audioDialog = MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Language")
                    .setOnCancelListener { player?.play() }
                    .setPositiveButton("Off Audio"){ self, _ ->
                        trackSelector.setParameters(trackSelector.buildUponParameters().setRendererDisabled(
                            C.TRACK_TYPE_AUDIO, true
                        ))
                        self.dismiss()
                    }
                    .setItems(tempTracks){_, position ->
                        Snackbar.make(detailBinding.root, audioList[position] + " Selected", 3000).show()
                        trackSelector.setParameters(trackSelector.buildUponParameters()
                            .setRendererDisabled(C.TRACK_TYPE_AUDIO, false)
                            .setPreferredAudioLanguage(audioTrack[position]))
                    }
                    .create()
                audioDialog.show()
                audioDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
                audioDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
            }
            bindingMF.subtitlesBtn.setOnClickListener {
                dialog.dismiss()
                player?.play()
                val subtitles = ArrayList<String>()
                val subtitlesList = ArrayList<String>()
                for(group in player!!.currentTracksInfo.trackGroupInfos){
                    if(group.trackType == C.TRACK_TYPE_TEXT){
                        val groupInfo = group.trackGroup
                        for (i in 0 until groupInfo.length){
                            subtitles.add(groupInfo.getFormat(i).language.toString())
                            subtitlesList.add("${subtitlesList.size + 1}. " + Locale(groupInfo.getFormat(i).language.toString()).displayLanguage
                                    + " (${groupInfo.getFormat(i).label})")
                        }
                    }
                }

                val tempTracks = subtitlesList.toArray(arrayOfNulls<CharSequence>(subtitlesList.size))
                val sDialog = MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Subtitles")
                    .setOnCancelListener { player?.play() }
                    .setPositiveButton("Off Subtitles"){ self, _ ->
                        trackSelector.setParameters(trackSelector.buildUponParameters().setRendererDisabled(
                            C.TRACK_TYPE_VIDEO, true
                        ))
                        self.dismiss()
                    }
                    .setItems(tempTracks){_, position ->
                        Snackbar.make(detailBinding.root, subtitlesList[position] + " Selected", 3000).show()
                        trackSelector.setParameters(trackSelector.buildUponParameters()
                            .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
                            .setPreferredTextLanguage(subtitles[position]))
                    }
                    .create()
                sDialog.show()
                sDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
                sDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
            }

            bindingMF.pipModeBtn.setOnClickListener {
                val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appOps.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName)==
                            AppOpsManager.MODE_ALLOWED
                } else { false }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    if (status) {
                        this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                        dialog.dismiss()
                        detailBinding.videoView.hideController()
                        player?.play()
                        pipStatus = 0
                    }
                    else{
                        val intent = Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS",
                            Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                }else{
                    Toast.makeText(this, "Feature Not Supported!!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    player?.play()
                }
            }

        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_DETAIL_FILM = "extra_detail_film"
        const val URL_VIDEO_DICODING = "https://github.com/dicodingacademy/assets/releases/download/release-video/VideoDicoding.mp4"
        private lateinit var trackSelector: DefaultTrackSelector
        private lateinit var player: ExoPlayer
        var pipStatus: Int = 0
    }
}