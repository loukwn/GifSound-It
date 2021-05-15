package com.kostaslou.gifsoundit.settings.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.kostaslou.gifsoundit.settings.SettingsContract
import com.loukwn.feat_settings.databinding.FragmentSettingsBinding

internal class SettingsViewImpl(
    inflater: LayoutInflater,
    container: ViewGroup?
) : SettingsContract.View {

    private val binding = FragmentSettingsBinding.inflate(inflater, container, false)
    private var listener: SettingsContract.Listener? = null

    init {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { listener?.onBackButtonPressed() }
        binding.modeSelector.container.setOnClickListener { listener?.onModeSelectorBgClicked() }
        binding.modeSelector.lightOption.setOnClickListener {
            listener?.onModeSelected(mode = AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.modeSelector.darkOption.setOnClickListener {
            listener?.onModeSelected(mode = AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.modeSelector.systemOption.setOnClickListener {
            listener?.onModeSelected(mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        binding.modeSelector.batteryOption.setOnClickListener {
            listener?.onModeSelected(mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
    }

    override fun collapseModeSelector() {
        binding.modeSelector.lightOption.isVisible = false
        binding.modeSelector.darkOption.isVisible = false
        binding.modeSelector.systemOption.isVisible = false
        binding.modeSelector.batteryOption.isVisible = false
        binding.modeSelector.bottomSpacer.isVisible = false

        binding.modeSelector.settingsThemeMoreButton.rotation = 0f
    }

    override fun expandModeSelector() {
        binding.modeSelector.lightOption.isVisible = true
        binding.modeSelector.darkOption.isVisible = true
        binding.modeSelector.systemOption.isVisible = true
        binding.modeSelector.batteryOption.isVisible = true
        binding.modeSelector.bottomSpacer.isVisible = true

        binding.modeSelector.settingsThemeMoreButton.rotation = 180f
    }

    override fun selectLightMode() {
        binding.modeSelector.lightOption.isSelected = true
        binding.modeSelector.darkOption.isSelected = false
        binding.modeSelector.systemOption.isSelected = false
        binding.modeSelector.batteryOption.isSelected = false
    }

    override fun selectDarkMode() {
        binding.modeSelector.lightOption.isSelected = false
        binding.modeSelector.darkOption.isSelected = true
        binding.modeSelector.systemOption.isSelected = false
        binding.modeSelector.batteryOption.isSelected = false
    }

    override fun selectSystemDefaultMode() {
        binding.modeSelector.lightOption.isSelected = false
        binding.modeSelector.darkOption.isSelected = false
        binding.modeSelector.systemOption.isSelected = true
        binding.modeSelector.batteryOption.isSelected = false
    }

    override fun selectBatterySaverMode() {
        binding.modeSelector.lightOption.isSelected = false
        binding.modeSelector.darkOption.isSelected = false
        binding.modeSelector.systemOption.isSelected = false
        binding.modeSelector.batteryOption.isSelected = true
    }

    override fun setModeSelectedStringRes(textRes: Int) {
        binding.modeSelector.selectedModeLabel.text = binding.root.context.getString(textRes)
    }

    override fun setListener(listener: SettingsContract.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: SettingsContract.Listener) {
        if (this.listener == listener) this.listener = null
    }

    override fun getRoot(): View = binding.root
}
