package com.kostaslou.gifsoundit.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.kostaslou.gifsoundit.MainActivity
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.util.RecyclerViewTools.Companion.withItemCount
import com.loukwn.postdata.FilterType
import org.hamcrest.Matchers.greaterThan

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

    fun textColorsOkWhenPostTypeIs(filterType: FilterType) {
        when (filterType) {
            FilterType.HOT -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_hot)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_inactive)
            }
            FilterType.NEW -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_new)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_inactive)
            }
            FilterType.TOP -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.newButton), R.color.list_menu_inactive)
                matchTextColor(onTextView(R.id.topButton), R.color.list_menu_top)
            }
        }
    }

    fun filterMenuIsGone() {
        checkVisibility(R.id.filterMenu, ViewMatchers.Visibility.GONE)
    }

    fun listHasData() {
        onRecyclerView(R.id.mainRecycler).check(withItemCount(greaterThan(0)))
        onRecyclerViewPosition(R.id.mainRecycler, 0).check(matches(hasDescendant(withId(R.id.postTitle))))
    }

//    fun listIsLoading() {
//        onRecyclerView(R.id.mainRecycler).check(withItemCount(greaterThan(0)))
//        onRecyclerViewPosition(R.id.mainRecycler, 0).check(matches(hasDescendant(withId(R.id.progress))))
//    }

    fun openGS(mActivityScenarioRule: ActivityScenarioRule<MainActivity>, func: OpenGSRobot.() -> Unit): OpenGSRobot {
        onRecyclerView(R.id.mainRecycler).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        return OpenGSRobot(mActivityScenarioRule).apply { func() }
    }
}
