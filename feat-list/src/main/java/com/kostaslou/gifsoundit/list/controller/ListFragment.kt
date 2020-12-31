package com.kostaslou.gifsoundit.list.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kostaslou.gifsoundit.list.NavigationTarget
import com.kostaslou.gifsoundit.list.view.ListContractViewImpl
import com.kostaslou.gifsoundit.list.viewmodel.ListViewModel
import com.loukwn.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
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
                    is NavigationTarget.OpenGs -> navigator.navigateToOpenGS(navTarget.gsQuery)
                }
            }
        })
    }
}
