package com.kostaslou.gifsoundit.list.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kostaslou.gifsoundit.list.view.ListContractViewImpl
import com.kostaslou.gifsoundit.list.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        return ListContractViewImpl(requireContext(), inflater, container)
            .also { viewModel.setView(it) }
            .getRootView()
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewModel.navigationEvents.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let { navTarget ->
                when (navTarget) {
                    is ListViewModel.NavigationTarget.OpenGs -> {
                        (activity as? Callback)?.navigateToOpenGS(navTarget.gsQuery)
                    }
                }
            }
        })
    }

    interface Callback {
        fun navigateToOpenGS(query: String?)
    }
}
