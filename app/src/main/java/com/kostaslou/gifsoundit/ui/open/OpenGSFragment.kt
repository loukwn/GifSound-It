package com.kostaslou.gifsoundit.ui.open

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.base.BaseFragment
import com.kostaslou.gifsoundit.util.ViewModelFactory
import javax.inject.Inject

class OpenGSFragment : BaseFragment() {

    // ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: OpenGSViewModel

    // bind xml
    override fun layoutRes() = R.layout.fragment_opengs


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OpenGSViewModel::class.java)

        initUI()
        restoreUI()
    }

    private fun initUI() {}

    private fun restoreUI() {}
}