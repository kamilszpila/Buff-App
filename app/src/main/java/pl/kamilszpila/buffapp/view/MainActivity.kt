package pl.kamilszpila.buffapp.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import pl.kamilszpila.buff.exception.InvalidBuffException
import pl.kamilszpila.buffapp.R
import pl.kamilszpila.buffapp.view_model.MainActivityViewModel


class MainActivity : AppCompatActivity() {


    companion object {
        const val VIDEO_URL =
            "https://buffup-public.s3.eu-west-2.amazonaws.com/video/toronto+nba+cut+3.mp4"
    }

    private lateinit var viewModel: MainActivityViewModel
    var mediaController: MediaController? = null
    var lastVideoPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        setupVideoView()
    }

    override fun onPause() {
        super.onPause()
        lastVideoPosition = videoView.currentPosition
        videoView.pause()

        buffView.stop()
        viewModel.stop()
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(lastVideoPosition)
        videoView.start()

        viewModel.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setFullScreenMode()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.buffData.observe(this, Observer {
            try {
                buffView.showBuff(it)
            } catch (e: InvalidBuffException) {
                e.printStackTrace()
            }
        })
    }

    private fun setupVideoView() {
        val uri = Uri.parse(VIDEO_URL)
        videoView.setVideoURI(uri)

        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
    }

    private fun setFullScreenMode() {
        val view = this.window.decorView
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
