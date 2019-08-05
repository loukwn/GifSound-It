package com.kostaslou.gifsoundit.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kostaslou.gifsoundit.di.extras.ViewModelKey
import com.kostaslou.gifsoundit.home.ui.HomeViewModel
import com.kostaslou.gifsoundit.home.util.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(listViewModel: HomeViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(OpenGSViewModel::class)
//    internal abstract fun bindDetailsViewModel(detailsViewModel: OpenGSViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(DetailsViewModel::class)
//    internal abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
