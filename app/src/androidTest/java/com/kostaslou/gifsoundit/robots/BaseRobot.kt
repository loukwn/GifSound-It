package com.kostaslou.gifsoundit.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.kostaslou.gifsoundit.util.RecyclerViewTools

open class BaseRobot {

    fun clickButton(resId: Int): ViewInteraction =
            onView((withId(resId))).perform(ViewActions.click())

    fun clickButton(text: String): ViewInteraction =
            onView((withText(text))).perform(ViewActions.click())

    fun checkVisibility(resId: Int, visibility: Visibility): ViewInteraction =
            onView((withId(resId))).check(matches(withEffectiveVisibility(visibility)))

    fun onTextView(resId: Int): ViewInteraction =
            onView(withId(resId))

    fun onRecyclerView(resId: Int): ViewInteraction =
            onView(withId(resId))

    private fun withRecyclerView(resId: Int): RecyclerViewTools =
            RecyclerViewTools(resId)

    fun onRecyclerViewPosition(resId: Int, position: Int): ViewInteraction =
            onView(withRecyclerView(resId).atPosition(position))

    fun matchTextColor(viewInteraction: ViewInteraction, color: Int): ViewInteraction =
            viewInteraction.check(matches(hasTextColor(color)))

    private fun matchText(viewInteraction: ViewInteraction, text: String): ViewInteraction =
            viewInteraction.check(matches(withText(text)))

    fun matchText(resId: Int, text: String): ViewInteraction =
            matchText(onTextView(resId), text)

    fun sleep(millis: Long = 500) = apply { Thread.sleep(millis) }
}
