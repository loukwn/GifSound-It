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
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        return ListContractViewImpl(requireContext(), inflater, container)
            .also { viewModel.setView(it) }
            .getRootView()
    }
}
