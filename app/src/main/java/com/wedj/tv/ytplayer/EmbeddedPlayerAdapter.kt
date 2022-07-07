/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.wedj.tv.ytplayer

import android.util.Log
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.data.entities.model.managevideo.VideoItem
import com.wedj.tv.playing.NextPlayingListingAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import kotlin.math.max


class EmbeddedPlayerAdapter(
    private val coroutineScope: LifecycleCoroutineScope,
    private var player: YouTubePlayer? = null,
    private var playerGlue: PlaybackTransportControlGlue<EmbeddedPlayerAdapter>? = null,
    private var nextListing_Adapter: NextPlayingListingAdapter? = null,
    private var callBack: UpdateYIdCallBack
    //   private var  viewModel: ManageVideoViewModel

) : PlayerAdapter() {


    private var playerState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN

    private var duration: Float = -1f

    private var currentSecond: Float = 0f

    private var loadedFraction: Float = 0f

    private var ready = false

    private var fastForwarding = false

    private var rewinding = false

    private var playlist = mutableListOf<ManageVideoResponse>()

    private var currentVideo = 0
    private var lastviedo = 0

    private val nextPlayingList = ArrayList<String>()


    /*init {
        imagesAdapter = NextPlayingListingAdapter(itemsList)

    }*/

    fun setPlayerGlue(playerGlue: PlaybackTransportControlGlue<EmbeddedPlayerAdapter>?) {
        this.playerGlue = playerGlue
    }

    fun setNextPlayingAdapter(nextListing_Adapter: NextPlayingListingAdapter?) {
        this.nextListing_Adapter = nextListing_Adapter
    }


    /*  fun callApi(viewModel: ManageVideoViewModel) {
          this.viewModel = viewModel
      }*/

    fun setPlayer(player: YouTubePlayer) {
        this.player = player

/*
        player.addListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                val videoId = "S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
            onNewState(state)
        }

        fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
          //  addToList("ERROR: " + error.name, playerStatesHistory)
        }*/

        player.addListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                this@EmbeddedPlayerAdapter.currentSecond = second

                callback.onCurrentPositionChanged(this@EmbeddedPlayerAdapter)
/*
                Log.d("PlayerAdapter", "onCurrentPositionChanged($second)")
*/
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                callback.onError(this@EmbeddedPlayerAdapter, 0, error.name)
//                Log.d("PlayerAdapter", "onError($error)")
            }

            override fun onPlaybackQualityChange(
                youTubePlayer: YouTubePlayer,
                playbackQuality: PlayerConstants.PlaybackQuality
            ) {
            }

            override fun onPlaybackRateChange(
                youTubePlayer: YouTubePlayer,
                playbackRate: PlayerConstants.PlaybackRate
            ) {
                Log.d(
                    "TAG",
                    "onPlaybackRateChange() called with: youTubePlayer = $youTubePlayer, playbackRate = $playbackRate"
                )

            }


            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@EmbeddedPlayerAdapter.ready = true

//                Log.d("PlayerAdapter", "onReady()")
                callback.onPreparedStateChanged(this@EmbeddedPlayerAdapter)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                this@EmbeddedPlayerAdapter.playerState = state
//                Log.d("PlayerAdapter", "onStateChange($state)")

                when (state) {
                    in arrayOf(
                        PlayerConstants.PlayerState.PLAYING,
                        PlayerConstants.PlayerState.PAUSED
                    ) -> {


                        this@EmbeddedPlayerAdapter.ready = true
                        callback.onPreparedStateChanged(this@EmbeddedPlayerAdapter)
                        callback.onPlayStateChanged(this@EmbeddedPlayerAdapter)

                        if (isPlaying){
                          //  if (playlist.size < currentVideo) {
                             try {
                                 Log.d("CurrentStatusChk", "playing stage${playlist[currentVideo].vdid}")

                                 callBack.onActionCheckVideoStatus(playlist[currentVideo].vdid)
                             }catch (ex : Exception){
                                 print( "Exception $ex")
                             }


                         //   }


                            //  callBack.onActionCheckVideoStatus("")


                        }
                    }



                    PlayerConstants.PlayerState.ENDED -> {



                      //  print("Ended_Viedo ${playlist[currentVideo].vdid}")

                        callBack.onActionFadeOut()

                        callBack.onActionUpdateYIdCallBack("end")

                        //  callBack.onActionUpdateYIdCallBack(playlist[currentVideo].vdid)
                        callback.onPlayCompleted(this@EmbeddedPlayerAdapter)


                        /* if (currentVideo > 0) {
                             callBack.onActionUpdateYIdCallBack(playlist[currentVideo - 1].vdid)
                         } else {
                             callBack.onActionUpdateYIdCallBack(playlist[currentVideo].vdid)

                         }*/




                        if (currentVideo >= 0) {
                            if (playlist.size - 1 == currentVideo) {
                                callBack.onActionBackToHome()
                            } else {
                                next()
                            }
                        }


                        //testing pending
                        Log.d(
                            "playlist",
                            "playlist_size =${playlist.size}"
                        )
                        Log.d(
                            "currentVideo",
                            "currentVideo_size_End =${currentVideo}"
                        )




                        //     obj.updateYtId(playlist[currentVideo --].vdid)

                      /*  if (playlist.size > 0){
                            callBack.onActionUpdateYIdCallBack(playlist[currentVideo - 1].vdid)
                        } else{
                            callBack.onActionUpdateYIdCallBack(playlist[currentVideo].vdid)
                        }*/

                    }
                    PlayerConstants.PlayerState.VIDEO_CUED -> {
                        callBack.onActionFadeIn()
                        this@EmbeddedPlayerAdapter.ready = true
                        callback.onPreparedStateChanged(this@EmbeddedPlayerAdapter)
                    }


                }
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                this@EmbeddedPlayerAdapter.duration = duration

//                Log.d("PlayerAdapter", "onVideoDuration($duration)")
                callback.onDurationChanged(this@EmbeddedPlayerAdapter)
                callback.onPlayStateChanged(this@EmbeddedPlayerAdapter)
            }

            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
            }

            override fun onVideoLoadedFraction(
                youTubePlayer: YouTubePlayer,
                loadedFraction: Float
            ) {
                this@EmbeddedPlayerAdapter.loadedFraction = loadedFraction

//                Log.d("PlayerAdapter", "onVideoLoadedFraction($loadedFraction)")
                callback.onBufferedPositionChanged(this@EmbeddedPlayerAdapter)
            }

        })


    }

    private fun onNewState(newState: PlayerConstants.PlayerState) {
        val playerState: String = playerStateToString(newState).toString()

    }

    private fun playerStateToString(state: PlayerState): String? {
        return when (state) {
            PlayerState.UNKNOWN -> "UNKNOWN"
            PlayerState.UNSTARTED -> "UNSTARTED"
            PlayerState.ENDED -> "ENDED"
            PlayerState.PLAYING -> "PLAYING"
            PlayerState.PAUSED -> "PAUSED"
            PlayerState.BUFFERING -> "BUFFERING"
            PlayerState.VIDEO_CUED -> "VIDEO_CUED"
            else -> "status unknown"
        }
    }


    override fun play() {
        stopFastForwardOrRewind()

        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun next() {
        Log.d("TAG", "next() called")



        stopFastForwardOrRewind()
        if (currentVideo < playlist.size - 1) {

            lastviedo = currentVideo

            currentVideo++
            playerGlue?.title = playlist[currentVideo].titles
            playerGlue?.subtitle = playlist[currentVideo].fullName

            callBack.onActionFadeIn()

            player?.loadVideo(playlist[currentVideo].vdid, 0.0f)
            print("nextCurrent_Viedo ${playlist[currentVideo].vdid}")
            Log.d("CurrentStatusChk", "now_playing ${playlist[currentVideo].vdid}")


            //  save current vid in prafrence
            callBack.onActionSaveCurrentPlayingYtId(playlist[currentVideo].vdid)

            callBack.onActionCurrentApi(playlist[currentVideo].vdid)

            callBack.onActionSaveCurrentVID(playlist[currentVideo].vdid)
            NextPlayingList()

            callBack.onActionUpdateYIdCallBack(playlist[lastviedo].vdid)

        }
    }

    override fun previous() {
        supportedActions

        stopFastForwardOrRewind()

        if (currentVideo > 0) {
            currentVideo--
            playerGlue?.title = playlist[currentVideo].titles
            playerGlue?.subtitle = playlist[currentVideo].fullName

            callBack.onActionFadeIn()

            player?.loadVideo(playlist[currentVideo].vdid, 0.0f)


            //  save current vid in prafrence
            callBack.onActionSaveCurrentPlayingYtId(playlist[currentVideo].vdid)

            NextPlayingList()
        }
    }

    private fun stopFastForwardOrRewind() {
        fastForwarding = false
        rewinding = false
        player?.seekTo(currentSecond)
    }

    private fun fastForwardOneSecond() {
        currentSecond = max(currentSecond + 1, 0f)
        callback.onCurrentPositionChanged(this@EmbeddedPlayerAdapter)

        if (currentSecond == duration) {
            fastForwarding = false
            player?.seekTo(currentSecond)
        }
    }

    private fun rewindOneSecond() {
        currentSecond = max(currentSecond - 1, 0f)
        callback.onCurrentPositionChanged(this@EmbeddedPlayerAdapter)

        if (currentSecond == 0f) {
            rewinding = false
            player?.seekTo(currentSecond)
        }
    }

    override fun fastForward() {
        fastForwarding = !fastForwarding
        rewinding = false

        if (fastForwarding) {
            if (isPlaying) {
                pause()
            }

            coroutineScope.launch {
                while (fastForwarding) {
                    delay(SEEK_UPDATE_INTERVAL)
                    fastForwardOneSecond()
                }
            }
        } else {
            stopFastForwardOrRewind()
        }
    }

    override fun rewind() {
        rewinding = !rewinding
        fastForwarding = false

        if (rewinding) {
            if (isPlaying) {
                pause()
            }

            coroutineScope.launch {
                while (rewinding) {
                    delay(SEEK_UPDATE_INTERVAL)
                    rewindOneSecond()
                }
            }
        } else {
            stopFastForwardOrRewind()
        }
    }

    override fun seekTo(positionInMs: Long) {
        player?.seekTo((positionInMs / MILLISECONDS).toFloat())
    }


    fun loadVideo(videoItem: VideoItem) {

        try {
//code that may throw exception

            var found = false
            this.playlist = videoItem.playlist
            this.currentVideo = videoItem.playlist.indexOf(videoItem.video)

            callBack.onActionFadeIn()
            player?.loadVideo(playlist[currentVideo].vdid, 0.0f)
            print("loadCurrent_Viedo ${playlist[currentVideo].vdid}")

            Log.d("CurrentStatusChk", "now_playing ${playlist[currentVideo].vdid}")
            //  save current vid in prafrence
            callBack.onActionSaveCurrentPlayingYtId(playlist[currentVideo].vdid)
            callBack.onActionCurrentApi(playlist[currentVideo].vdid)
            callBack.onActionSaveCurrentVID(playlist[currentVideo].vdid)


            NextPlayingList()

            //Code for Handle outofIndexBound Exception
            /* for (itm in 0..playlist.size) {
                 if (playlist[itm].vdid == playlist[currentVideo].vdid) {
                     found = true
                     break
                 }
             }
             if (found){
                 println(" is found.")
                 callBack.onActionFadeIn()
                 player?.loadVideo(playlist[currentVideo].vdid, 0.0f)
                 //  save current vid in prafrence
                 callBack.onActionSaveCurrentPlayingYtId(playlist[currentVideo].vdid)
                 NextPlayingList()
             }
             else{
                 println(" is not found.")
                 callBack.onActionBackToHome()
             // Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();

             }*/
        } catch (e: Exception) {
            println(" is Exception. $e")
            callBack.onActionBackToHome()
//code that handles exception
        }
    }

    fun NextPlayingList() {

        var pending_length = playlist.size - currentVideo
        var count = 1

        try{
            if (pending_length != 0) {

                if (pending_length >= 6) {
                    nextPlayingList.clear()

                    for (item in currentVideo..currentVideo + 5) {

                        var len_title = playlist[item].titles.length
                        var fullName_title = playlist[item].fullName.length
                        var orderNumber = item

                        var TilteTwentyChars: String
                        var fullNameTenChars: String

                        print("stringLength $len_title")
                        if (len_title >= 20) {
                            TilteTwentyChars = playlist[item].titles.substring(0, 20);

                            if (fullName_title >= 10) {
                                fullNameTenChars = playlist[item].fullName.substring(0, 10);
                            } else {
                                fullNameTenChars = playlist[item].fullName
                            }

                            /*  if(count == 0){
                                  nextPlayingList.add(TilteTwentyChars+"... " + fullNameTenChars)
                              }else{
                                  nextPlayingList.add("$count  " +TilteTwentyChars+"... " + fullNameTenChars)
                              }*/
                            nextPlayingList.add("$count  " + TilteTwentyChars + "... " + fullNameTenChars)



                            print("stringFullName ${TilteTwentyChars + "... " + fullNameTenChars}")
                        } else {
                            if (fullName_title >= 10) {
                                fullNameTenChars = playlist[item].fullName.substring(0, 10);
                            } else {
                                fullNameTenChars = playlist[item].fullName
                            }

/*
                    if(count == 0){
                        nextPlayingList.add(playlist[item].titles+"... " + fullNameTenChars)
                    }else{
                        nextPlayingList.add("$count  " +playlist[item].titles+"... " + fullNameTenChars)
                    }*/

                            nextPlayingList.add("$count  " + playlist[item].titles + "... " + fullNameTenChars)

                            //  nextPlayingList.add(playlist[item].titles+"... " + playlist[item].fullName)
                        }

                        count++

                    }

                    nextListing_Adapter?.ItemList(nextPlayingList);

                } else {
                    nextPlayingList.clear()
                    for (item in currentVideo..currentVideo + (pending_length - 1)) {
                        // nextPlayingList.add(playlist[item].titles)
                        var len_title = playlist[item].titles.length
                        var fullName_title = playlist[item].fullName.length
                        var orderNumber = item + 1;

                        var TilteTwentyChars: String
                        var fullNameTenChars: String

                        print("stringLength $len_title")
                        if (len_title >= 20) {
                            TilteTwentyChars = playlist[item].titles.substring(0, 20);

                            if (fullName_title >= 10) {
                                fullNameTenChars = playlist[item].fullName.substring(0, 10);
                            } else {
                                fullNameTenChars = playlist[item].fullName
                            }

                            /* if(count == 0){
                                 nextPlayingList.add(TilteTwentyChars+"... " + fullNameTenChars)
                             }else{
                                 nextPlayingList.add("$count  " +TilteTwentyChars+"... " + fullNameTenChars)

                             }*/
                            nextPlayingList.add("$count  " + TilteTwentyChars + "... " + fullNameTenChars)


                            print("stringFullName ${TilteTwentyChars + "... " + fullNameTenChars}")
                        } else {
                            if (fullName_title >= 10) {
                                fullNameTenChars = playlist[item].fullName.substring(0, 10);
                            } else {
                                fullNameTenChars = playlist[item].fullName
                            }

                            /* if(count == 0){
                                 nextPlayingList.add(playlist[item].titles+"... " + fullNameTenChars)
                             }else{
                                 nextPlayingList.add("$count  " +playlist[item].titles+"... " + fullNameTenChars)

                             }*/
                            nextPlayingList.add("$count  " + playlist[item].titles + "... " + fullNameTenChars)


                            //  nextPlayingList.add(playlist[item].titles+"... " + playlist[item].fullName)
                        }
                        count++
                    }
                    nextListing_Adapter?.ItemList(nextPlayingList);

                }

            }

        }catch(e: Exception){
            Log.d("NextPlaying", "NextPlayingList() called$e")
        }

    }

    override fun isPrepared() = ready

    override fun isPlaying() = playerState == PlayerConstants.PlayerState.PLAYING

    override fun getDuration() = (duration * MILLISECONDS).toLong()

    override fun getCurrentPosition() = (currentSecond * MILLISECONDS).toLong()

    override fun getBufferedPosition() = (loadedFraction * duration * MILLISECONDS).toLong()


    companion object {

        /** Number of milliseconds in a second. */
        private const val MILLISECONDS = 1000

        /** Number of milliseconds after which fast forward or rewind is moved by one second. */
        private const val SEEK_UPDATE_INTERVAL = 200L
    }


    /*un returnCurrentPosition(): Int {
       return currentVideo
    }*/


    /*   fun viedoNextCallBack() {
           next()
       }*/


}