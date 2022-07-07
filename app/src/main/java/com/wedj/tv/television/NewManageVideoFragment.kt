package com.wedj.tv.television

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.wedj.tv.R
import com.wedj.tv.custom.HeaderItemModel
import com.wedj.tv.custom.IconHeaderItem
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.data.entities.model.managevideo.VideoItem
import com.wedj.tv.home.ManageUserError
import com.wedj.tv.home.ManageVideoState
import com.wedj.tv.home.ManageVideoViewModel
import com.wedj.tv.home.NetworkError
import com.wedj.tv.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NewManageVideoFragment : BrowseSupportFragment() {

    private val TAG = javaClass.simpleName
    private val viewModel: ManageVideoViewModel by viewModels()
    private val BACKGROUND_UPDATE_DELAY = 300L
    private val ACTION_WATCH = 1L
    private val videoList = ArrayList<ManageVideoResponse>()
    private val LogOutList = ArrayList<ManageVideoResponse>()

    private lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBackground()
        setHeadersTransitionOnBackEnabled(true)
        // isHeadersTransitionOnBackEnabled(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        repeatApiCall().start()
    }

    @SuppressLint("RestrictedApi")
    private fun handle(state: ManageVideoState) {
        Log.d(TAG, "handle() called with: state = ${state}")
        when {
            state.isLoading -> {
//                progressDialog.showLoadingDialog(requireActivity(), null)
            }
            state.isSuccess -> {
//                progressDialog.dismissLoadingDialog()
                Log.d(TAG, "handle: ${state.response}")
                if (!backgroundManager.isAttached) {
                    backgroundManager.attach(requireActivity().window)
                }
                val list = state.response

                /* Create header for each album*/
                val header = HeaderItemModel(
                    0L,
                    requireActivity().resources.getString(R.string.home),
                    R.drawable.ic_baseline_home
                )
                val header1 = HeaderItemModel(
                    1L,
                    requireActivity().resources.getString(R.string.home_msg),
                    0
                )

                val header2 = HeaderItemModel(
                    2L,
                    requireActivity().resources.getString(R.string.settings),
                    R.drawable.ic_baseline_settings
                )
                val header3 = HeaderItemModel(
                    3L,
                    requireActivity().resources.getString(R.string.profile),
                    R.drawable.ic_baseline_account_circle
                )

                print("frsh_list_size ${list?.size}")

                if (videoList.size > 0) {
                    val newList = list?.filterNot { videoList.contains(it) }

                    print("filtter_list_size ${newList?.size}")

                    if (newList?.isNotEmpty() == true) {
                        videoList.addAll(newList)

                    }
                } else {
                    list?.forEach { item ->
                        videoList.add(item)
                    }

                    /*  var slect_postion = selectedPosition
                      Log.d(TAG, "slect_postion = $slect_postion")*/

                    //reverse list
                   videoList.reverse()

                }

                val listRowAdapter = ArrayObjectAdapter(MoviePresenter()).apply {
                    /**
                     * Add all the collection's metadata to the row's adapter
                     * */
                    setItems(videoList, null)
                }

                //      val = logoutAdapter
                //   LogOutList.add("LogOut")
                if (LogOutList.size < 1) {
                    LogOutList.add(
                        ManageVideoResponse(
                            0,
                            "0",
                            0,
                            "Logout",
                            "",
                            "",
                            "",
                            false,
                            "Logout"
                        )
                    )

                }

                val LogoutRowAdapter = ArrayObjectAdapter(MoviePresenter()).apply {
                    /**
                     * Add all the collection's metadata to the row's adapter
                     * */
                    setItems(LogOutList, null)
                }
                val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
                /**
                 * Add a list row for the <header, row adapter> pair
                 * */
                rowsAdapter.add(ListRow(header, listRowAdapter))
                rowsAdapter.add(ListRow(header2, LogoutRowAdapter))
                rowsAdapter.add(ListRow(header1, ArrayObjectAdapter()))
//                rowsAdapter.add(ListRow(header2, ArrayObjectAdapter()))
                adapter = rowsAdapter

            }
            state.uiError != null -> {
//                progressDialog.dismissLoadingDialog()
                showErrorMessage(state.uiError)
//                showErrorMessage(getString(R.string.session_expires);
                Log.d(TAG, "handle: Call 1 ${state.uiError}")
            }
            state.isLogout -> {
//              findNavController().popBackStack(R.id.action_home_to_login,true)
                navigation(Paths.URI_LOGIN)
//                val intent= Intent(activity, MainActivity::class.java)
//                val bundle = activity?.let {
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(it)
//                        .toBundle()
//                }
//                startActivity(intent, bundle)
//                activity?.finish()
            }
        }
    }

    private fun setupUIElements() {
        // Badge, when set, takes precedent over title
//        title = getString(R.string.app_name)
//        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = true
        // set headers background color
        brandColor = ContextCompat.getColor(requireContext(), R.color.green)
        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return IconHeaderItem()
            }
        })

    }

    private fun initializeBackground() {
        backgroundManager = BackgroundManager.getInstance(requireActivity())


        val metrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
        onItemViewSelectedListener =
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                if (item is ManageVideoResponse) {
                    viewLifecycleOwner.lifecycleScope.launch {
//                        item.isSelected = !item.isSelected
                        delay(BACKGROUND_UPDATE_DELAY)
                        val backgroundImageUrl = getVideoBannerUrl(item.vdid)
                        loadDrawable(
                            requireActivity(),
                            backgroundImageUrl,
                            R.drawable.default_background,
                            metrics.widthPixels,
                            metrics.heightPixels
                        ) {
                            backgroundManager.drawable = it
                        }

                    }
                }
            }
        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

        //        throw RuntimeException("Test Crash") // Force a crash

                val data = item as ManageVideoResponse

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


        setupUIElements()
    }


    private fun showErrorMessage(error: ManageUserError) {
        val message = when (error) {

            NetworkError -> getString(R.string.network_connection)
            else -> getString(R.string.unknown_error)
//            else -> getString(R.string.session_expires)  // change Message
        }
        backgroundManager.release()
        Log.d(
            TAG,
            "showErrorMessage() called with: error = ${backgroundManager.isAutoReleaseOnStop}"
        )
        navigation(Paths.ID_ERROR, Bundle().apply {
            putString(Constants.ERROR_MESSAGE, message)
            putInt(Constants.ERROR_FLAG, Constants.ERROR_MANAGE_FLAG_VALUE)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundManager.release()
    }

    private fun callApi() {
        viewModel.manageUser("")
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

}