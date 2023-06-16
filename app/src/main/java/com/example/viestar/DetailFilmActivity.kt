package com.example.viestar

import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.List
import java.util.*
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.*

class DetailFilmActivity : AppCompatActivity(), TextOutput {

    override fun onCues(cues: MutableList<Cue>) {
        // Hapus teks subtitle sebelumnya

            subtitleView.setCues(cues)


    }

    private lateinit var subtitles: List<Cue>

    private val detailBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailFilmBinding.inflate(layoutInflater)
    }

    var properties: DialogProperties = DialogProperties()
    private var clickedSubtitleWord: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(detailBinding.root)

        supportActionBar?.title = "Detail "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)
        Log.d("Tes", data.toString())

//        detailBinding.overlayText.setOnClickListener {
//            showSubtitleDialog(
//                "Contoh Kalimat\n" +
//                        " \n" +
//                "Use this knife to cut the rope\n" +
//                        "Gunakan pisau ini untuk memotong talinya\n " +
//                        " \n" +
//                        "I bought this knife yesteday\n" +
//                        "Aku membeli pisau ini kemarin\n" +
//                        " \n" +
//                        "This knife is very sharp\n" +
//                        "Pisau ini sangat tajam"
//            )
//        }

        setMoreButton()

        val text = "have you been listening to anything ?"
        val words = text.split(" ")

        val ss = SpannableString(text)

        words.forEach { word ->
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
//                    MaterialAlertDialogBuilder(this@DetailFilmActivity)
//                        .setTitle(word)
//                        .setMessage("")
//                        .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
//                            // Respond to neutral button press
//                        }
//                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
//                            // Respond to positive button press
//                        }
//                        .show()

                    val dialogBuilder = MaterialAlertDialogBuilder(this@DetailFilmActivity)
                        .setTitle(word)
                        .setMessage(word)


                    val dialog = dialogBuilder.create()

                    val window = dialog.window
                    val layoutParams = window?.attributes

                    dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

// Mengatur lebar dialog (misalnya, 80% dari debar layar)
                    layoutParams?.width = (resources.displayMetrics.widthPixels * 0.5).toInt()

// Mengatur tinggi dialog (misalnya, menggunakan ukuran tertentu dalam piksel)
                    layoutParams?.height = resources.getDimensionPixelSize(R.dimen.dialog_height)

                    dialog.setOnShowListener {
                        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
                        layoutParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                        positiveButton.layoutParams = layoutParams
                        positiveButton.setBackgroundResource(R.drawable.dialog_button_background)
                    }

                    window?.attributes = layoutParams

                    dialog.show()

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }

            val startIndex = text.indexOf(word)
            val endIndex = startIndex + word.length
            ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        detailBinding.overlayText.text = ss
        detailBinding.overlayText.movementMethod = LinkMovementMethod.getInstance()
        detailBinding.overlayText.highlightColor = Color.WHITE

    }

    private fun showSubtitleDialog(subtitleText: String) {
        MaterialAlertDialogBuilder(this@DetailFilmActivity)
            .setTitle(
                "Knife / \n" +
                  "Pisau"
            )
            .setMessage(subtitleText)
            .setNeutralButton("OK") { dialog, which ->
                // Respon terhadap klik tombol OK
            }
            .show()
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


    private lateinit var players: SimpleExoPlayer
    private lateinit var subtitleView: SubtitleView
    private fun initializePlayerSubtitle(subtitleUrl: String) {
        val videoUri = intent.getParcelableExtra<Uri>(EXTRA_DETAIL_FILM)

        val subtitleUri = Uri.parse(subtitleUrl)
        val subtitle = MediaItem.Subtitle(
            subtitleUri,
            MimeTypes.APPLICATION_SUBRIP,
            "en",
            C.SELECTION_FLAG_DEFAULT
        )

//        val subtitleContent = readSrtFile(subtitleUri)
//        Log.d("Subtitle Content", subtitleContent)

//        readSrtFile(subtitleUri)

        val mediaItem = MediaItem.Builder()
            .setUri(videoUri)
            .setSubtitles(listOf(subtitle))
            .build()

        val trackSelector = DefaultTrackSelector(this)
        val player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        subtitleView = detailBinding.videoView.subtitleView!!
        subtitleView.setCues(listOf())

//        Log.d("tes subtile", subtitleView.setCues(listOf()).toString())


        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        detailBinding.videoView.player = player

    }

    private fun readSrtFile(subtitleUrl: Uri) {
        Log.d("Url Subtitle", subtitleUrl.toString())
        val subtitleFile = File("/mnt/sdcard/Bloodhounds.S01E08.1080p.NF.WEB-DL.DDP5.1.Atmos.H.264-XEBEC.cc.srt")
        val subtitleText = StringBuilder()

        try {
            val inputStream = FileInputStream(subtitleFile)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                subtitleText.append(line).append("\n")
            }

            reader.close()
            inputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val subtitleString = subtitleText.toString()
        Log.d("tes subtitle", subtitleString)

    }

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
//        hideSystemUI()
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
//                        dialog.dismiss()

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
                val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appOps.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(), packageName)==
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



    private fun showClickedSubtitleDialog() {
        clickedSubtitleWord?.let { word ->
            // Tampilkan dialog dengan kata yang diklik
            MaterialAlertDialogBuilder(this)
                .setTitle("Subtitle Word")
                .setMessage("Clicked Word: $word")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        const val EXTRA_DETAIL_FILM = "extra_detail_film"
        const val URL_VIDEO_DICODING = "https://github.com/dicodingacademy/assets/releases/download/release-video/VideoDicoding.mp4"
        private lateinit var trackSelector: DefaultTrackSelector
        private lateinit var player: ExoPlayer
        var pipStatus: Int = 0
    }

}


