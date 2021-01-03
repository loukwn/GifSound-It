package com.kostaslou.gifsoundit.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kostaslou.gifsoundit.settings.view.SettingsViewMvcImpl
import com.kostaslou.gifsoundit.settings.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(viewModel)
        return SettingsViewMvcImpl(inflater, container)
            .also { viewModel.setView(it) }
            .getRootView()
    }
}
