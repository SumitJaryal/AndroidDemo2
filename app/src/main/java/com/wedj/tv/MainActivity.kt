package com.wedj.tv

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.databinding.ActivityMainBinding
import com.wedj.tv.util.Paths
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val TAG = javaClass.simpleName
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var isBackPress = false

    @Inject
    lateinit var preferenceManager: PreferenceManager

    companion object {
        var context: Context? = null
    }

    private val destinationChangeListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.navigation_splash -> {
                    isBackPress = false
                    Log.d(TAG, "called with: controller = Splash")
                }
                R.id.navigation_home -> {
                }
                R.id.navigation_home_new -> {
                    isBackPress = false
                    Log.d(TAG, "called with: controller = New Home")
                }
                R.id.navigation_login -> {
                    isBackPress = false
//                    binding.header.root.visibility = View.VISIBLE
////                    bindingHeader.ivLeft.visibility = View.VISIBLE
//                    bottomNavStatus(View.GONE)
//                    setTitle(getString(R.string.login))
////                    textTitle = getString(R.string.login)
//                    setToolbarWithBackPressButton()
                    Log.d(TAG, "called with: controller = Login")
                }
                R.id.navigation_base_menu -> {
                    isBackPress = false
                    Log.d(TAG, "called with: controller = Base menu")
                }
                R.id.navigation_yt_playing -> {
                    isBackPress = true
                    Log.d(TAG, "called with: controller = yt_playing")
                }
                else -> {
                    isBackPress = false
//                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    Log.d(TAG, "called with: controller = else ${destination.label}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val accessToken = "0DfEwu62O-V-2GB_eC4dMKuo5N7GsRR4tu1lq6beniu775lqxFyCmvRSVlPaDlTOyzMIQ4Yud_9bcfYnNllnDjb1txlfBHUnPkMREwkSMZOTln2EikCMO0IWP_qFiytD0WgaXTKIyQ91FNCuLosn2C93A-6DHajTU9GrZI7n3pnIS1KsWdszuPGSAPumFaN0UXM6yIhZuNwp3klkYF-vRWPJb-KOrPcXXcvs2poes0kfEGSDarpu4WxusgkJPUuD8tomF34fCylsfdZhUHthSSBmU7ChXSlLT56xszE4qngGQ7q7DeZ4-JkNvLy3fx2e5OQEUPvdIrBA5zKnTjuyiA"
//        val tokenType = "bearer"
//        val userName = "androidTest"
//        val authToken = "$tokenType $accessToken"
//        preferenceManager.setUserDetail(authToken, tokenType, accessToken, userName)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val config = AppBarConfiguration(navController.graph)
        navController.addOnDestinationChangedListener(destinationChangeListener)
        viewModel.stateFlow.onEach { handle(it) }.launchIn(lifecycleScope)

    }

    override fun onResume() {
        super.onResume()
        if (context == null) {
            context = this
        }
    }

    private fun handle(state: MainViewState) {

        Log.d(TAG, "handle() called with: state = $state")
        when (state.mainNavigation) {
            Home -> {
                Log.d(TAG, "handle() called with: state = ${state.mainNavigation}")
                navigate(Paths.URI_BASE_MENU)
            }
            Login -> {
                Log.d(TAG, "handle() called with: state = ${state.mainNavigation}")
                navigate(Paths.URI_LOGIN)
            }
            ForgotPassword -> {

            }
            Dashboard -> {

            }
        }
    }

    /*
    * pass the uri & navigate to particular screen
    * */

    private fun navigate(uri: Uri) {
        navController.navigate(
            uri, NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }

    override fun onBackPressed() {
        if (isBackPress)
            super.onBackPressed()
        else finish()
    }

}