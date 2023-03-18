package com.loukwn.gifsoundit.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.transition.MaterialSharedAxis
import com.loukwn.gifsoundit.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class CreateFragment : Fragment() {
    private val viewModel: CreateContract.ViewModel by viewModels<CreateViewModel>()

    @Inject
    internal lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward = */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward = */ false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.receiveAsFlow().collectLatest {
                    when (it) {
                        CreateContract.Event.Close -> navigator.goBack()
                        is CreateContract.Event.OpenGs -> {
                            navigator.navigateToOpenGS(it.url, false)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext()).apply {
            isTransitionGroup = true
        }
        composeView.setContent {
            val uiModel = viewModel.uiModelFlow.collectAsState(CreateContract.UiModel())

            CreateView(uiModel = uiModel.value, listener = viewModel)
        }

        return composeView
    }
}
