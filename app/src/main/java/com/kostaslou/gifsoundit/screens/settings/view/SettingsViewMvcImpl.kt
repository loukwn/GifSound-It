package com.kostaslou.gifsoundit.screens.settings.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.screens.settings.controller.SettingsUiModel

class SettingsViewMvcImpl(
    val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?
) : SettingsViewMvc {

    private var listener: SettingsViewMvc.Listener? = null
    private val view = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun setDataToViews(uiModel: SettingsUiModel) {}

    override fun setListener(listener: SettingsViewMvc.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: SettingsViewMvc.Listener) {
        if (this.listener == listener) this.listener = null
    }

    override fun getRootView(): View {
        return view
    }
}
