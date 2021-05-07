package com.kostaslou.gifsoundit.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

open class BaseRobot {

    fun clickButton(resId: Int): ViewInteraction =
            onView((withId(resId))).perform(ViewActions.click())

    fun clickButton(text: String): ViewInteraction =
            onView((withText(text))).perform(ViewActions.click())

    fun checkVisibility(resId: Int, visibility: Visibility): ViewInteraction =
            onView((withId(resId))).check(matches(withEffectiveVisibility(visibility)))

    fun onTextView(resId: Int): ViewInteraction =
            onView(withId(resId))

    fun matchTextColor(viewInteraction: ViewInteraction, color: Int): ViewInteraction =
            viewInteraction.check(matches(hasTextColor(color)))

    fun sleep(millis: Long = 500) = apply { Thread.sleep(millis) }
}
