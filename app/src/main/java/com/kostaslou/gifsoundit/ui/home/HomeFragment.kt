package com.kostaslou.gifsoundit.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.base.BaseFragment
import com.kostaslou.gifsoundit.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    // bind xml
    override fun layoutRes() = R.layout.fragment_home


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        toolbarTitle.text = viewModel.showHello()
    }
}