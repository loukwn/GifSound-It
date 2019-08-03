package com.kostaslou.gifsoundit.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.util.RecyclerViewTools.Companion.withItemCount
import com.kostaslou.gifsoundit.util.commons.PostType
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

    fun textColorsOkWhenPostTypeIs(postType: PostType) {
        when (postType) {
            PostType.HOT -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.colorOrange)
                matchTextColor(onTextView(R.id.newButton), R.color.colorGrayDark)
                matchTextColor(onTextView(R.id.topButton), R.color.colorGrayDark)
            }
            PostType.NEW -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.colorGrayDark)
                matchTextColor(onTextView(R.id.newButton), R.color.colorGreen)
                matchTextColor(onTextView(R.id.topButton), R.color.colorGrayDark)
            }
            PostType.TOP -> {
                matchTextColor(onTextView(R.id.hotButton), R.color.colorGrayDark)
                matchTextColor(onTextView(R.id.newButton), R.color.colorGrayDark)
                matchTextColor(onTextView(R.id.topButton), R.color.colorBlue)
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

    infix fun openGS(func: OpenGSRobot.() -> Unit): OpenGSRobot {
        onRecyclerView(R.id.mainRecycler).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        return OpenGSRobot().apply { func() }
    }
}