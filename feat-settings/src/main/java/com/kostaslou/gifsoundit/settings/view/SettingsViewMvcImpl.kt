package com.kostaslou.gifsoundit.settings.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kostaslou.gifsoundit.settings.SettingsContract
import com.kostaslou.gifsoundit.settings.controller.SettingsUiModel
import com.loukwn.feat_settings.R

class SettingsViewMvcImpl(
    val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?
) : SettingsContract.View {

    private var listener: SettingsContract.Listener? = null
    private val view = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun setDataToViews(uiModel: SettingsUiModel) {}

    override fun setListener(listener: SettingsContract.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: SettingsContract.Listener) {
        if (this.listener == listener) this.listener = null
    }

    override fun getRootView(): View {
        return view
    }
}
