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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.viewModels
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.WriterException
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.wedj.tv.R
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.data.entities.model.managevideo.VideoItem
import com.wedj.tv.domain.base.BaseUseCase
import com.wedj.tv.home.ManageUserError
import com.wedj.tv.home.ManageVideoState
import com.wedj.tv.home.ManageVideoViewModel
import com.wedj.tv.home.NetworkError
import com.wedj.tv.playing.NextPlayingListingAdapter
import com.wedj.tv.util.*
import com.wedj.tv.util.Constants.ARGUMENT_VIDEO
import com.wedj.tv.util.Constants.DEFAULT_ROOM_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


/** Handles video playback with media controls. */
@AndroidEntryPoint
class VideoPlaybackFragment : PlaybackSupportFragment(), UpdateYIdCallBack {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val TAG = javaClass.simpleName
    private var playerGlue: PlaybackTransportControlGlue<EmbeddedPlayerAdapter>? = null
    private val viewModel: ManageVideoViewModel by viewModels()
    private val videoItem: VideoItem by lazy { arguments?.getSerializable(ARGUMENT_VIDEO) as VideoItem }
    private val newItemsArray: ArrayList<ManageVideoResponse> = ArrayList()
    private var isCurrentVideoDeleteFromServer = true
    private val nextPlayingList = ArrayList<String>()
    private lateinit var nextListing_Adapter: NextPlayingListingAdapter
    lateinit var fade_image: ImageView
    var playingCurrentViedo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val videoItem = arguments?.getSerializable(ARGUMENT_VIDEO) as VideoItem

        playerGlue = VideoPlaybackControlGlue(
            requireActivity(),
            EmbeddedPlayerAdapter(lifecycleScope, callBack = this)
        ).apply {
            host = PlaybackSupportFragmentGlueHost(this@VideoPlaybackFragment)

            isControlsOverlayAutoHideEnabled = true

            title = videoItem.video.titles
            subtitle = videoItem.video.fullName

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val root = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        val videoItem = arguments?.getSerializable(ARGUMENT_VIDEO) as? VideoItem ?: return root
        val playerView = inflater.inflate(R.layout.fragment_playback_video, root, false)
        //   val playerView = inflater.inflate(R.layout.custom_youtube_ui, root, false)
        val player = playerView.findViewById(R.id.player) as YouTubePlayerView

        //  getLifecycle().addObserver(player);

        val ivQr = playerView.findViewById(R.id.iv_qr) as ImageView
        fade_image = playerView.findViewById(R.id.fade_image) as ImageView

        val tvRoom = playerView.findViewById(R.id.tv_room_code) as TextView

        val recyclerView = playerView.findViewById(R.id.player_list_recyclerview) as RecyclerView

        //   val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        nextListing_Adapter = NextPlayingListingAdapter(nextPlayingList)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = nextListing_Adapter
        //  prepareItems()


        var roomId =
            if (videoItem.video.roomText.isBlank()) DEFAULT_ROOM_ID else videoItem.video.roomText
        tvRoom.text = getString(R.string.room_id, roomId)
        Log.d(
            TAG, "chk_room_id=$roomId"
        )


        var qrgEncoder: QRGEncoder
        var custom_url: String

        if (roomId != null && roomId != Config.BASE_URL) {
            custom_url = Config.QR_URL1 + roomId
        } else {
            custom_url = Config.QR_URL
        }

        //  roomId = Config.QR_URL // if (roomId == DEFAULT_ROOM_ID) Config.BASE_URL else videoItem.video.roomText
        qrgEncoder = QRGEncoder(custom_url, null, QRGContents.Type.TEXT, 100)
        try {
            // getting our qrcode in the form of bitmap.
            val bitmap = qrgEncoder.encodeAsBitmap()
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            ivQr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString())
        }
        lifecycle.addObserver(player)

        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        repeatApiCall().start()

        // just for test
        //   updateYtIdApi("a18py61_F_w")

        player.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                playerGlue?.playerAdapter?.setPlayerGlue(playerGlue)
                playerGlue?.playerAdapter?.setNextPlayingAdapter(nextListing_Adapter)
                playerGlue?.playerAdapter?.setPlayer(youTubePlayer)
                playerGlue?.playerAdapter?.loadVideo(videoItem)
                // playerGlue?.playerAdapter?.callApi(viewModel)
            }
        })

        root.addView(playerView, 0)

        backgroundType = BG_LIGHT
        return root
    }


    override fun onPause() {
        super.onPause()
        playerGlue?.pause()
    }

    private fun handle(state: ManageVideoState) {
        Log.d(TAG, "handle() called with: state = ${state}")
        when {
            state.isLoading -> {
//                progressDialog.showLoadingDialog(requireActivity(), null)
            }
            state.isSuccess -> {

                when (state.responseType) {
                    BaseUseCase.ResponseType.VIDEO_STATUS -> {
                        state.responseCheckVideoStatus?.let {
                            Log.d(TAG, "handle() called ResultID ${BaseUtil.jsonFromModel(it)}")

                            Log.d("CurrentStatusChk", "Result ${preferenceManager.playingVid}")

                            if (it.ResultID == 1) {

                               // playerGlue?.playerAdapter?.viedoNextCallBack()
                                Log.d("CurrentStatusChk", "Result ${preferenceManager.playingVid}")

                                if (playerGlue!!.isPlaying){

                                    for (item in videoItem.playlist){

                                        if (item.vdid == preferenceManager.currentVid){
                                            videoItem.playlist.remove(item)
                                            playerGlue?.next()

                                        }
                                     //   playerGlue?.next()
                                    }


                                }



                            /*    if (isCurrentVideoDeleteFromServer) {
                                    Log.d(TAG, "handle() called isCurrentVideoDeleteFromServer $isCurrentVideoDeleteFromServer")
                                    isCurrentVideoDeleteFromServer = false

                                }*/

                            }
                        }
                    }
                    BaseUseCase.ResponseType.GET_ROOM_VIDEO -> {
                        Log.d(TAG, "handle: ${state.response}")

                        val list = state.response

                        Log.d(TAG, "handle() called with: state = $state")
                        Log.d(TAG, "new_listSize = ${list!!.size}")
                        Log.d(TAG, "old_listSize = ${videoItem.playlist.size}")


                        //   val newList = list?.filterNot { videoItem.playlist.contains(it) }
                        if (list!!.size > 0) {


                            if (videoItem.playlist.size != list?.size) {
                            //    videoItem.playlist.clear()
                                // reverselist
                              if (playerGlue?.isPlaying == true) {

                               /*   if (list.size > videoItem.playlist.size){
                                      videoItem.playlist.addAll(list)
                                  }else{
                                      videoItem.playlist.addAll(list.reversed())
                                  }*/

                                  if (  videoItem.playlist.size > 0) {
                                      val newList = list.filterNot { videoItem.playlist.contains(it) }

                                      print("filtter_list_size================== ${newList?.size}")


                                      if (newList.isNotEmpty()) {
                                          videoItem.playlist.addAll(newList)
                                      }
                                  }

                                  //  videoItem.playlist.addAll(list)
                              }else{
                                  videoItem.playlist.clear()

                                  videoItem.playlist.addAll(list.reversed())

                              }
                                //    videoItem.playlist.addAll(list)
                                playerGlue?.playerAdapter?.NextPlayingList()


                            } else {

                                for (index in 0..list.size - 1) {

                                    Log.d(TAG, "enterforLoop")

                                    if (videoItem.playlist[index].queuestID == list!![index].queuestID) {
                                        continue
                                    } else {
                                        Log.d(TAG, "diffrence")

                                      //  videoItem.playlist.clear()
                                        //reverse list
                                        if (playerGlue?.isPlaying == true){

                                           /* if (list.size> videoItem.playlist.size){
                                                videoItem.playlist.addAll(list)
                                            }else{
                                                videoItem.playlist.addAll(list.reversed())
                                            }*/

                                            if (  videoItem.playlist.size > 0) {
                                                val newList = list.filterNot { videoItem.playlist.contains(it) }

                                                print("filtter_list_size================== ${newList?.size}")


                                                if (newList.isNotEmpty()) {
                                                    videoItem.playlist.addAll(newList)
                                                }
                                            }


                                        }else{
                                            videoItem.playlist.clear()
                                            videoItem.playlist.addAll(list.reversed())
                                        }


                                        //  videoItem.playlist.addAll(list)

                                        break

                                    }
                                }
                                playerGlue?.playerAdapter?.NextPlayingList()

                            }
                            Log.d(TAG, " playingCurrentViedo $playingCurrentViedo")

                            // know deleted viedo and move next viedo
                            /*   if (playingCurrentViedo!= null){
                                   var found = false

                                   for (index in 0..videoItem.playlist.size - 1) {

                                       if (videoItem.playlist[index].vdid == playingCurrentViedo) {
                                          found = true
                                           break
                                       }
                                   }
                                   if(found){
                                       print("is present in the list")
                                   }else{
                                       print("not found in the list")

                                       if (playerGlue?.playerAdapter!!.isPlaying){
                                           playerGlue?.playerAdapter?.pause()
                                           playerGlue?.playerAdapter?.next()
                                       }

                                   }
                               }*/

                        }
                    }
                }

//                progressDialog.dismissLoadingDialog()

            }
            state.uiError != null -> {
//                progressDialog.dismissLoadingDialog()
                showErrorMessage(state.uiError)
//                showErrorMessage(getString(R.string.session_expires);
                Log.d(TAG, "handle: Call 1 ${state.uiError}")
            }
        }
    }

    private fun showErrorMessage(error: ManageUserError) {
        val message = when (error) {

            NetworkError -> getString(R.string.network_connection)
            else -> getString(R.string.unknown_error)
//            else -> getString(R.string.session_expires)  // change Message
        }
        navigation(Paths.ID_ERROR, Bundle().apply {
            putString(Constants.ERROR_MESSAGE, message)
            putInt(Constants.ERROR_FLAG, Constants.ERROR_MANAGE_FLAG_VALUE)
        })
    }

    private fun callApi() {
        viewModel.getRoomVideo("")
    }

    /* private fun updateYtIdApi(ytId:String) {
         viewModel.updateYtId(ytId )
     }*/

    /**
     * Api Call every 1 minute
     * To get the updated video playlist
     * */
    private fun repeatApiCall(): Job {
        return viewLifecycleOwner.lifecycle.coroutineScope.launch {
            while (isActive) {
                callApi()
                delay(Constants.API_DELAY_CALL)
            }
        }

    }

    override fun onActionUpdateYIdCallBack(id: String) {
        Log.d(TAG, "onActionUpdateYIdCallBack() called with: id = $id")
        viewModel.updateYtId(id)

    }

    override fun onActionSaveCurrentPlayingYtId(YtId: String) {
        playingCurrentViedo = YtId
        preferenceManager.setNowplayingVId(YtId)


    }

    override fun onActionFadeIn() {

        fade_image.visibility = View.VISIBLE
        //loading our custom made animations
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        fade_image.startAnimation(animation)

        //starting the animation
        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this,MainActivity::class.java))
            fade_image.visibility = View.GONE

        }, 1000)
    }

    override fun onActionFadeOut() {
        fade_image.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        fade_image.startAnimation(animation)
        //textview will be invisible after the specified amount
        // of time elapses, here it is 1000 milliseconds


        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this,MainActivity::class.java))
            fade_image.visibility = View.GONE

        }, 1000)


    }


    override fun onActionBackToHome() {

        findNavController().navigate(Paths.URI_BASE_MENU,
            navOptions {
                anim {
                    exit = R.anim.fade_out
                    enter = R.anim.slide_in_v_pop
                    popEnter = R.anim.fade_in
                    popExit = R.anim.slide_out_v
                }
            })

    }

    private fun repeatApiCall(vId: String): Job {
        return viewLifecycleOwner.lifecycle.coroutineScope.launch {
            while (isActive) {
                viewModel.checkVideoStatus(vId)
                delay(Constants.CURRENT_STATUS_CALL)
            }
        }
    }

    override fun onActionCheckVideoStatus(YtId: String) {
        preferenceManager.saveLastPlayingVId(YtId)
        repeatApiCall(YtId)
    }

    override fun onActionCurrentApi(YtId: String) {
       viewModel.currentViedoApi(YtId)
    }

    override fun onActionSaveCurrentVID(vId: String) {
        preferenceManager.saveCurrentVId(vId)
    }

}