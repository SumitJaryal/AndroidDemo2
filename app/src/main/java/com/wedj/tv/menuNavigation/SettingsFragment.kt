package com.wedj.tv.menuNavigation

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.wedj.tv.R
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.home.ManageUserError
import com.wedj.tv.home.ManageVideoState
import com.wedj.tv.home.ManageVideoViewModel
import com.wedj.tv.home.NetworkError
import com.wedj.tv.television.MoviePresenter
import com.wedj.tv.util.Constants
import com.wedj.tv.util.NavigationMenuCallback
import com.wedj.tv.util.Paths
import com.wedj.tv.util.navigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : BrowseSupportFragment() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val TAG = javaClass.simpleName
    private val viewModel: ManageVideoViewModel by viewModels()
    private val BACKGROUND_UPDATE_DELAY = 300L
    private lateinit var navigationMenuCallback: NavigationMenuCallback
    private val logoutList = ArrayList<ManageVideoResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = preferenceManager.roomCode
        badgeDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_account_circle)

        headersState = HEADERS_DISABLED
        initializeBackground()
    }

    override fun onStart() {
        super.onStart()
        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handle(state: ManageVideoState) {
        Log.d(TAG, "handle() called with: state = ${state}")
        when {
            state.isLoading -> {
//                progressDialog.showLoadingDialog(requireActivity(), null)
            }
            state.isSuccess -> {
//                progressDialog.dismissLoadingDialog()
                Log.d(TAG, "handle() called with: state = isSuccess")

            }
            state.uiError != null -> {
//                progressDialog.dismissLoadingDialog()
                showErrorMessage(state.uiError)
//                showErrorMessage(getString(R.string.session_expires);
                Log.d(TAG, "handle: Call 1 ${state.uiError}")
            }
            state.isLogout -> {
                navigation(Paths.URI_LOGIN)
            }
        }
    }

    private fun initializeBackground() {
        setAdapter()
        onItemViewSelectedListener =
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)
                itemViewHolder?.view?.setOnKeyListener { v, keyCode, event ->
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
                val data = item as ManageVideoResponse
                if (data.logout?.isNotBlank() == true) {
                    viewModel.logout("")
                }
            }
    }

    private fun setAdapter() {
        /* Create header for each album*/
        val header = HeaderItem(
            0L, requireActivity().resources.getString(R.string.settings)
        )
        val headerInfo = HeaderItem(
            1L,
            requireActivity().resources.getString(R.string.home_msg)
        )
        logoutList.add(
            ManageVideoResponse(
                0, "0", 0, "Logout", "",
                "", "", false, "Logout"
            )
        )
        val listRowAdapter = ArrayObjectAdapter(MoviePresenter()).apply {
            /**
             * Add all the collection's metadata to the row's adapter
             * */
            setItems(logoutList, null)
        }

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        /**
         * Add a list row for the <header, row adapter> pair
         * */
        rowsAdapter.add(ListRow(header, listRowAdapter))
//        rowsAdapter.add(ListRow(headerInfo, ArrayObjectAdapter()))
        adapter = rowsAdapter

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