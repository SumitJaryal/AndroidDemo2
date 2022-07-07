package com.wedj.tv.menuNavigation

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.androijo.tvnavigation.NavigationMenu
import com.androijo.tvnavigation.interfaces.FragmentChangeListener
import com.androijo.tvnavigation.interfaces.NavigationStateListener
import com.androijo.tvnavigation.utils.Constants
import com.wedj.tv.R
import com.wedj.tv.base.BaseFragment
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.databinding.FragmentBaseNavigationBinding
import com.wedj.tv.util.NavigationMenuCallback
import com.wedj.tv.util.getVideoBannerUrl
import com.wedj.tv.util.loadDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseNavigationFragment : BaseFragment<FragmentBaseNavigationBinding>(),
    NavigationStateListener, FragmentChangeListener, NavigationMenuCallback {

    private var currentSelectedFragment = Constants.nav_menu_movies
    private lateinit var navMenuFragment: NavigationMenu
    private lateinit var manageVideoFragment: ManageVideoFragment
    private lateinit var settingsFragment: SettingsFragment


    override val viewBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBaseNavigationBinding
        get() = FragmentBaseNavigationBinding::inflate

    override fun onViewBindingCreated(
        view: View, binding: FragmentBaseNavigationBinding, savedInstanceState: Bundle?
    ) {
        navMenuFragment = NavigationMenu()
        fragmentReplacer(binding.navFragment.id, navMenuFragment)
        manageVideoFragment = ManageVideoFragment()
        fragmentReplacer(binding.mainFL.id, manageVideoFragment)
        activity?.supportFragmentManager?.addFragmentOnAttachListener { fragmentManager, fragment ->
            when (fragment) {
                is ManageVideoFragment -> {
                    Log.d(TAG, "onAttachFragment() called with: fragment = ManageVideoFragment")
                    fragment.setNavigationMenuCallback(this)
                }
                is SettingsFragment -> {
                    fragment.setNavigationMenuCallback(this)
                }
//            is MusicFragment -> {
//                fragment.setNavigationMenuCallback(this)
//            }
                is NavigationMenu -> {
                    Log.d(TAG, "onAttachFragment() called with: fragment = NavigationMenu")
                    fragment.setFragmentChangeListener(this)
                    fragment.setNavigationStateListener(this)
                }
            }
        }
    }

    override fun navMenuToggle(toShow: Boolean) {
        try {
            if (toShow) {
                binding.navFragment.setBackgroundResource(R.drawable.ic_nav_bg_open)
                binding.mainFL.clearFocus()
                binding.navFragment.requestFocus()
                navEnterAnimation()
                navMenuFragment.openNav()
            } else {
                binding.navFragment.setBackgroundResource(R.drawable.ic_nav_bg_closed)
                binding.navFragment.clearFocus()
                binding.mainFL.requestFocus()
                navMenuFragment.closeNav()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeBackground(item: Any?) {
        when (item) {
            is ManageVideoResponse -> {
                val metrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
                val backgroundImageUrl = getVideoBannerUrl(item.vdid)
                activity?.let { it ->
                    loadDrawable(
                      //  it, backgroundImageUrl, R.drawable.bg_default,
                        it, backgroundImageUrl, R.drawable.we_trans_without_logo,
                        metrics.widthPixels, metrics.heightPixels
                    ) {
                        binding.mainFL.background = it
                        /*binding.mainFL.background =
                            activity?.let { ContextCompat.getDrawable(it, R.color.white) }*/
                    }
                }
            }
            else -> {
                binding.mainFL.background =
                  //  activity?.let { ContextCompat.getDrawable(it, R.drawable.bg_default) }
                    activity?.let { null }
            }
        }
    }

    override fun switchFragment(fragmentName: String?) {
        binding.navFragment.setBackgroundResource(R.drawable.ic_nav_bg_closed)
        when (fragmentName) {
            Constants.nav_menu_movies -> {
                manageVideoFragment = ManageVideoFragment()
                fragmentReplacer(binding.mainFL.id, manageVideoFragment)
                manageVideoFragment.restoreSelection()
            }
            Constants.nav_menu_settings -> {
                settingsFragment = SettingsFragment()
                fragmentReplacer(binding.mainFL.id, settingsFragment)
                settingsFragment.restoreSelection()
            }
//            Constants.nav_menu_music -> {
//                musicFragment = MusicFragment()
//                fragmentReplacer(binding.mainFL.id, musicFragment)
////                musicFragment.selectFirstItem()
//            }
        }
    }

    override fun onStateChanged(expanded: Boolean, lastSelected: String?) {
        if (!expanded) {
            binding.navFragment.setBackgroundResource(R.drawable.ic_nav_bg_closed)
            binding.navFragment.clearFocus()

            when (currentSelectedFragment) {
                Constants.nav_menu_movies -> {
                    currentSelectedFragment = Constants.nav_menu_movies
//                    moviesFragment.restoreSelection()
                }
                Constants.nav_menu_settings -> {
                    currentSelectedFragment = Constants.nav_menu_settings
//                    settingsFragment.selectFirstItem()
                }
                Constants.nav_menu_music -> {
                    currentSelectedFragment = Constants.nav_menu_music
//                    musicFragment.selectFirstItem()
                }
            }
        } else {
            //do
        }
    }

    private fun fragmentReplacer(containerId: Int, fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.replace(containerId, fragment)
            ?.commit()
    }

    private fun navEnterAnimation() {
        val animate = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left)
        binding.navFragment.startAnimation(animate)
    }
}