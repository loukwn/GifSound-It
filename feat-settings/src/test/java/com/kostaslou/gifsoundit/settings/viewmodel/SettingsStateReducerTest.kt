package com.kostaslou.gifsoundit.settings.viewmodel

import com.kostaslou.gifsoundit.settings.Action
import com.kostaslou.gifsoundit.settings.State
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SettingsStateReducerTest {
    private val sut = SettingsStateReducer()

    @Test
    fun `GIVEN ModeSelected action WHEN map THEN update the mode in the state`() {
        val action = Action.ModeSelected(0)
        val state = State(modeSelectorCollapsed = false, currentMode = 1, aboutCollapsed = true)

        val newState = sut.reduce(state, action)

        assertEquals(0, newState.currentMode)
    }

    @Test
    fun `GIVEN ModeBgClicked action WHEN map THEN toggle the modeSelectorCollapsed in the state`() {
        val action = Action.ModeBgClicked
        val state = State(modeSelectorCollapsed = false, currentMode = 1, aboutCollapsed = true)

        val newState = sut.reduce(state, action)

        assertEquals(true, newState.modeSelectorCollapsed)
    }

    @Test
    fun `GIVEN AboutBgClicked action WHEN map THEN toggle the modeSelectorCollapsed in the state`() {
        val action = Action.AboutBgClicked
        val state = State(aboutCollapsed = false, currentMode = 1, modeSelectorCollapsed = false)

        val newState = sut.reduce(state, action)

        assertEquals(true, newState.aboutCollapsed)
    }

    @Test
    fun `GIVEN Created action WHEN map THEN return the same state`() {
        val action = Action.Created
        val state = State(modeSelectorCollapsed = false, currentMode = 1, aboutCollapsed = false)

        val newState = sut.reduce(state, action)

        assert(newState == state)
    }
}
