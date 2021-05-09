package com.loukwn.postdata.di

import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.PostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

// Todo: Rethink about the component here
@Suppress("unused")
@InstallIn(ActivityRetainedComponent::class)
@Module
internal abstract class PostDataBindingModule {

    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
