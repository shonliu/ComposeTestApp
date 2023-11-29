package com.example.genesistestproject

import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer

class MainViewModel: ViewModel() {

    lateinit var player: SimpleExoPlayer

    fun playPause(): Boolean {
        return if (player.isPlaying) {
            player.pause()
            false
        } else {
            player.play()
            true
        }
    }

    fun playerPause(): Boolean {
        player.pause()
        return false
    }

    fun scrollTo(seekTo: Long) {
        player.seekTo(seekTo)

    }

    fun getDuration() = player.duration

    fun getCurrentPosition() = player.currentPosition

    fun initExoPlayer(exoPlayer: SimpleExoPlayer) {
        player = exoPlayer
    }


}