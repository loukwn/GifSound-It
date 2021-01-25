package com.kostaslou.gifsoundit.opengs.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kostaslou.gifsoundit.opengs.view.OpenGSViewImpl
import com.kostaslou.gifsoundit.opengs.viewmodel.OpenGSViewModel
import com.loukwn.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenGSFragment : Fragment() {

    private val viewModel: OpenGSViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        return OpenGSViewImpl(requireContext(), inflater, container).also {
            viewModel.setView(it)
            setupViewModel()
            lifecycle.addObserver(it.getSoundYoutubePlayerView())
        }.getRoot()
    }

    private fun setupViewModel() {
        val query = arguments?.getString(Navigator.PARAM_OPENGS_QUERY) ?: return
        val fromDeepLink = arguments?.getBoolean(Navigator.PARAM_OPENGS_FROM_DEEP_LINK) ?: return

        viewModel.setup(query = query, isFromDeepLink = fromDeepLink)
    }
}
