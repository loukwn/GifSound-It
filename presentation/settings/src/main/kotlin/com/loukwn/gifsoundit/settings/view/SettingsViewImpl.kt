package com.loukwn.gifsoundit.settings.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.loukwn.gifsoundit.settings.SettingsContract
import com.loukwn.gifsoundit.settings.databinding.FragmentSettingsBinding

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
        binding.aboutOption.container.setOnClickListener { listener?.onAboutBgClicked() }
        binding.ossContainer.setOnClickListener { listener?.onOssContainerClicked() }
    }

    override fun collapseModeSelector() {
        binding.modeSelector.modeSelectorGroup.isVisible = false
        binding.modeSelector.settingsThemeMoreButton.rotation = 0f
    }

    override fun expandModeSelector() {
        binding.modeSelector.modeSelectorGroup.isVisible = true
        binding.modeSelector.settingsThemeMoreButton.rotation = 180f
    }

    override fun collapseAbout() {
        binding.aboutOption.aboutContentGroup.isVisible = false
        binding.aboutOption.settingsThemeAboutButton.rotation = 0f
    }

    override fun expandAbout() {
        binding.aboutOption.aboutContentGroup.isVisible = true
        binding.aboutOption.settingsThemeAboutButton.rotation = 180f
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
