package com.kostaslou.gifsoundit.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.ui.home.HomeViewModel
import com.kostaslou.gifsoundit.di.extras.ViewModelKey
import com.kostaslou.gifsoundit.ui.open.OpenGSFragment
import com.kostaslou.gifsoundit.ui.open.OpenGSViewModel
import com.kostaslou.gifsoundit.util.ViewModelFactory
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(listViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OpenGSViewModel::class)
    internal abstract fun bindDetailsViewModel(detailsViewModel: OpenGSViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(DetailsViewModel::class)
//    internal abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}