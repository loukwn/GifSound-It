package com.loukwn.gifsoundit.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.loukwn.gifsoundit.list.view.ListViewImpl
import com.loukwn.gifsoundit.list.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpCustomOnBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        postponeEnterTransition()
        return ListViewImpl(
            context = requireContext(),
            inflater = inflater,
            container = container,
            onRecyclerViewPopulated = { startPostponedEnterTransition() }
        )
            .also { viewModel.setView(it) }
            .getRoot()
    }

    /**
     * Sets up a backpressed dispatcher to disallow the activity to dismiss unless
     * the options layout at the top is not dismissed
     */
    private fun setUpCustomOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.onBackPressed()) {
                        this.remove()
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }
}
