package com.loukwn.gifsoundit.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialSharedAxis
import com.loukwn.gifsoundit.create.databinding.FragmentCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@AndroidEntryPoint
class CreateFragment : Fragment(), CreateContract.Listener {
    private val viewModel: CreateViewModel by viewModels()
    private var _binding: FragmentCreateBinding? = null
    private val binding: FragmentCreateBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)

        binding.root.setContent {
            val uiModel = viewModel.uiModelFlow.collectAsState(UiModel.default()).value
            CreateView(uiModel, this)
        }

        return binding.root
    }

    override fun onGoPressed() {
        viewModel.goPressed()
    }

}