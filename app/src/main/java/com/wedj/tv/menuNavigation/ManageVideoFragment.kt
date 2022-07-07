package com.wedj.tv.menuNavigation

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.wedj.tv.R
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.data.entities.model.managevideo.VideoItem
import com.wedj.tv.domain.base.BaseUseCase
import com.wedj.tv.home.*
import com.wedj.tv.television.MoviePresenter
import com.wedj.tv.television.TextCard
import com.wedj.tv.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ManageVideoFragment : BrowseSupportFragment() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val TAG = javaClass.simpleName
    private val viewModel: ManageVideoViewModel by viewModels()
    private val BACKGROUND_UPDATE_DELAY = 300L
    private val ACTION_WATCH = 1L
    private lateinit var navigationMenuCallback: NavigationMenuCallback
    private val videoList = ArrayList<ManageVideoResponse>()
    private val textList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = HEADERS_DISABLED
        title = Config.BASE_URL
        badgeDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_account_circle)
        initializeBackground()
    }

    override fun onStart() {
        super.onStart()
        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        repeatApiCall().start()
    }

    private fun handle(state: ManageVideoState) {
        when {
            state.isLoading -> {
//                progressDialog.showLoadingDialog(requireActivity(), null)
            }
            state.isSuccess -> {
//                progressDialog.dismissLoadingDialog()
                when (state.responseType) {
                    BaseUseCase.ResponseType.GET_ROOMS -> {
                        state.responseRoom?.let {
                            if (it.isNotEmpty()) {
                                title = state.responseRoom[0].roomCode.toString()
                                viewModel.getRoomVideo("")
                            }
                        }
                    }

                    BaseUseCase.ResponseType.MY_FOLDER -> {
                        state.responseMyFloder?.let {
                            if (it.isNotEmpty()) {

                                val fId = it[0].folderID
                                print("folder_id = $fId")

                                if (fId != null){

                                    viewModel.getAllMyPlaylist(fId)
                                }

                            }
                        }
                    }

                    BaseUseCase.ResponseType.GET_ROOM_VIDEO -> {
                        state.response?.let {

                            if (it.isEmpty()){

                                //call myfloder api

                                viewModel.myFolder("")

                            }

                            if (it.isNotEmpty()) {
                                /* Create header for each album*/
                              /*  val header = HeaderItem(
                                    0L,
                                    requireActivity().resources.getString(R.string.home)
                                )*/ val header = HeaderItem(
                                    0L,
                                    ""
                                )
                                val headerInfo = HeaderItem(
                                    1L,
                                    requireActivity().resources.getString(R.string.home_msg)
                                )

                                print("fresh_list_size ================== ${it.size}")

                                if (videoList.size > 0) {
                                    val newList = it.filterNot { videoList.contains(it) }

                                    print("filtter_list_size================== ${newList?.size}")


                                    if (newList.isNotEmpty()) {
                                        videoList.addAll(newList)
                                    }
                                } else {

                                    var lastPlayingVId = preferenceManager.playingVid

                                    Log.d(TAG, "lastPlayingVId = $lastPlayingVId")



                                    it.forEach { item ->
                                        videoList.add(item)

                                    }


                                    if (lastPlayingVId.isNotEmpty()){
                                        it.forEach { item ->
                                            if (lastPlayingVId.isNotEmpty() && item.vdid == lastPlayingVId){
                                                videoList.add(videoList.size ,item)
                                                videoList.remove(item)
                                            }
                                        }
                                    }






                                //reverse list
                                    videoList.reverse()

                                    /*if (videoList.size> 0){

                                        var lastPlayingVId = preferenceManager.playingVid
                                        Log.d(TAG, "lastPlayingVId = $lastPlayingVId")
                                        for (itm in videoList){
                                            if (lastPlayingVId.isNotEmpty() && itm.vdid == lastPlayingVId){
                                                videoList.add(0 ,itm)
                                            }
                                        }
                                    }
*/


                                }

                                val listRowAdapter = ArrayObjectAdapter(MoviePresenter()).apply {
                                    /**
                                     * Add all the collection's metadata to the row's adapter
                                     * */
                                    setItems(videoList, null)
                                }


                                if(textList.size < 1){
                                    textList.add("item")
                                }

                                val listTextAdapter = ArrayObjectAdapter(TextCard()).apply {
                                    setItems(textList,null)
                                }

                                val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
                                /**
                                 * Add a list row for the <header, row adapter> pair
                                 * */
                                rowsAdapter.add(ListRow(header, listRowAdapter))
                            //    rowsAdapter.add(ListRow(headerInfo, ArrayObjectAdapter()))
                                rowsAdapter.add(ListRow(header, listTextAdapter))
                                adapter = rowsAdapter
                            }
                        }
                    }
                    else -> {}
                }

            }
            state.uiError != null -> {
//                progressDialog.dismissLoadingDialog()

                if(  state.uiError == EmptyListFound){
                    Log.d(TAG, "DataNotFound = ${state.uiError}")
                    viewModel.myFolder("")

                }else{
                    showErrorMessage(state.uiError)
//                showErrorMessage(getString(R.string.session_expires);
                    Log.d(TAG, "handle: Call 1 ${state.uiError}")
                }


            }
            state.isLogout -> {
                navigation(Paths.URI_LOGIN)
            }
        }
    }

    private fun initializeBackground() {
        onItemViewSelectedListener =
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)
                itemViewHolder?.view?.setOnKeyListener { v, keyCode, event ->
                    Log.d(
                        TAG,
                        "initializeBackground() keyCode = $keyCode,"
                    )
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                if (indexOfItem == 0) {
                                    navigationMenuCallback.navMenuToggle(true)
                                }
                            }
                        }
                    }
                    false
                }
                navigationMenuCallback.changeBackground(item)

            }
        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->


                if (item is ManageVideoResponse ){

                    val data = item as ManageVideoResponse
                    Log.d(
                        TAG,
                        "initializeBackground() called with: item = ${BaseUtil.jsonFromModel(item)}"
                    )
                    if (data.logout?.isNotBlank() == true) {
                        viewModel.logout("")
                    } else {
                        val bundle = Bundle()
                        bundle.putSerializable(
                            Constants.ARGUMENT_VIDEO,
                            VideoItem(data, videoList)
                        )
                        navigation(Paths.ID_YT_PLAYING, bundle)
                    }
                }


            }
    }

    private fun showErrorMessage(error: ManageUserError) {
        val message = when (error) {


            NetworkError -> getString(R.string.network_connection)
            DataNotFound -> getString(R.string.home_msg)
            RoomNotFound -> getString(R.string.home_room_msg)
            else -> getString(R.string.unknown_error)
//            else -> getString(R.string.session_expires)  // change Message
        }
        navigation(Paths.ID_ERROR, Bundle().apply {
            putString(Constants.ERROR_MESSAGE, message)
            putInt(Constants.ERROR_FLAG, Constants.ERROR_LOGIN_FLAG_VALUE)
        })
    }

    private fun callApi() {
//        viewModel.manageUser("")
        viewModel.getRoom("")
    }

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

    fun setNavigationMenuCallback(callback: NavigationMenuCallback) {
        this.navigationMenuCallback = callback
    }

    /**
     * this function can put focus or select a specific item in a specific row
     */
    fun restoreSelection() {
        setSelectedPosition(
            0,
            true,
            object : ListRowPresenter.SelectItemViewHolderTask(0) {
                override fun run(holder: Presenter.ViewHolder?) {
                    super.run(holder)
                    holder?.view?.postDelayed({
                        holder.view.requestFocus()
                    }, 10)
                }
            })
    }
}