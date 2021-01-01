package com.loukwn.feat_settings.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.loukwn.feat_settings.SettingsContract
import com.loukwn.feat_settings.view.SettingsViewMvcImpl

class SettingsFragment : Fragment(), SettingsContract.Listener {

    // view
    private var viewMvc: SettingsContract.View? = null
    private lateinit var uiModel: SettingsUiModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc = SettingsViewMvcImpl(requireContext(), inflater, container)
        return viewMvc?.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc?.setListener(this)
    }

    override fun onStop() {
        viewMvc?.removeListener(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewMvc = null
    }

    override fun onBackButtonPressed() {
    }

    override fun onThemeSelected(mode: Int) {
    }
}
