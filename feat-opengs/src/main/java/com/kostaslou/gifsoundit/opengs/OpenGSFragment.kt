package com.kostaslou.gifsoundit.opengs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialContainerTransform
import com.kostaslou.gifsoundit.opengs.managers.ExoplayerManager
import com.kostaslou.gifsoundit.opengs.view.OpenGSViewImpl
import com.kostaslou.gifsoundit.opengs.viewmodel.OpenGSViewModel
import com.loukwn.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OpenGSFragment : Fragment() {

    private val viewModel: OpenGSViewModel by viewModels()
    @Inject
    lateinit var exoManager: ExoplayerManager

    private var view: OpenGSContract.View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        val transitionName = requireArguments().getString(Navigator.PARAM_OPENGS_TRANSITION_NAME)
        view = OpenGSViewImpl(
            context = requireContext(),
            inflater = inflater,
            container = container,
            transitionName = transitionName,
            exoManager = exoManager,
        ).also {
            viewModel.setView(it)
            lifecycle.addObserver(it.getSoundYoutubePlayerView())
        }
        return view?.getRoot()
    }
}
