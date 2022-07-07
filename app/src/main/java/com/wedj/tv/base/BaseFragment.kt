package com.wedj.tv.base

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.wedj.tv.R
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.util.ProgressDialog
import javax.inject.Inject


abstract class BaseFragment<FragmentBinding : ViewBinding> : Fragment() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private var _binding: FragmentBinding? = null
//    private lateinit var themeColor: String
    protected lateinit var progressDialog: ProgressDialog
    protected val binding
        get() = _binding
            ?: throw IllegalStateException("Cannot access view binding in after view destroyed and before view creation")
    protected val TAG = javaClass.simpleName

    /**
     * Bind view here
     */
    /*   @MainThread
       @NotNull*/
    abstract val viewBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBinding

    /**
     * Use this method instead of onViewCreated
     */
    @MainThread
    abstract fun onViewBindingCreated(
        view: View,
        binding: FragmentBinding,
        savedInstanceState: Bundle?
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = viewBinding.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRetainInstance(true);
        progressDialog = ProgressDialog()
        onViewBindingCreated(view, binding, savedInstanceState)
//        themeColor = preferenceManager.themeColor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navigation(uri: Uri) {
        findNavController().navigate(uri,
            navOptions {
                anim {
                    exit = R.anim.fade_out
                    enter = R.anim.slide_in_v_pop
                    popEnter = R.anim.fade_in
                    popExit = R.anim.slide_out_v
                }
            })
    }

    fun navigation(id: Int) {
        findNavController().navigate(id,null,
            navOptions {
                anim {
                    exit = R.anim.fade_out
                    enter = R.anim.slide_in_v_pop
                    popEnter = R.anim.fade_in
                    popExit = R.anim.slide_out_v
                }
            })
    }

    fun navigation(id: Int, bundle: Bundle) {
        findNavController().navigate(id, bundle,
            navOptions {
                anim {
                    exit = R.anim.fade_out
                    enter = R.anim.slide_in_v_pop
                    popEnter = R.anim.fade_in
                    popExit = R.anim.slide_out_v
                }
            })
    }

    private val actionRequestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            handlePermissionResult(it)
        }
    private val actionRequestSinglePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            handleSinglePermissionResult(it)
        }

    protected open fun handleSinglePermissionResult(it: Boolean) {
        Log.d(TAG, "handleSinglePermissionResult(BaseFragment) called with: it = $it")
    }

    protected open fun handlePermissionResult(permissionsResult: MutableMap<String, Boolean>) {
        Log.d(
            TAG,
            "handlePermissionResult(BaseFragment) called with: permissionsResult = $permissionsResult"
        )
    }


    fun requestMultiplePermissions(permissions: Array<String>) {
        actionRequestPermissions.launch(permissions)
    }

    fun requestSinglePermission(permission: String) {
        actionRequestSinglePermission.launch(permission)
    }
//
//    fun setToggleBackgroung(toggleButton: ToggleButton) {
//        toggleButton.setBackgroundColor(Color.parseColor(themeColor))
//    }
//
//    fun setButtonTheme(button: MaterialButton) {
//        button.setBackgroundColor(Color.parseColor(themeColor))
//    }
}