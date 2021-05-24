package com.loukwn.gifsoundit.robots

import androidx.test.espresso.matcher.ViewMatchers
import com.loukwn.gifsoundit.R
import com.loukwn.gifsoundit.postdata.FilterTypeDTO

fun home(func: HomeRobot.() -> Unit) = HomeRobot().apply { func() }

enum class TopDialogButton {
    HOUR, DAY, WEEK, MONTH, YEAR, ALL
}

class HomeRobot : BaseRobot() {

    fun more() {
        clickButton(R.id.moreButton)
    }

    fun hot() {
        clickButton(R.id.hotButton)
    }

    fun new() {
        clickButton(R.id.newButton)
    }

    fun top() {
        clickButton(R.id.topButton)
    }

    fun clickDialogButton(button: TopDialogButton) {
        val buttonText = when (button) {
            TopDialogButton.HOUR -> "Hour"
            TopDialogButton.DAY -> "Day"
            TopDialogButton.WEEK -> "Week"
            TopDialogButton.MONTH -> "Month"
            TopDialogButton.YEAR -> "Year"
            TopDialogButton.ALL -> "All"
        }

        clickButton(buttonText)
    }

    fun textColorsOkWhenPostTypeIs(filterType: FilterTypeDTO) {
        when (filterType) {
            is FilterTypeDTO.Hot -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_hot)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_inactive)
            }
            is FilterTypeDTO.New -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_new)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_inactive)
            }
            is FilterTypeDTO.Top -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_top)
            }
        }
    }

    fun filterMenuIsGone() {
        checkVisibility(R.id.filterMenu, ViewMatchers.Visibility.GONE)
    }
}
