package com.loukwn.gifsoundit.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

@Suppress("unused")
class RecyclerViewTools(private val recyclerViewId: Int) {

    companion object {
        fun withItemCount(expectedCount: Int): ViewAssertion {
            return withItemCount(`is`(expectedCount))
        }

        private fun withItemCount(matcher: Matcher<Int>): ViewAssertion {
            return ViewAssertion { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter
                assertThat(adapter!!.itemCount, matcher)
            }
        }
    }

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    private fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            var childView: View? = null

            override fun describeTo(description: Description) {
                val idDescription = recyclerViewId.toString()
                description.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View): Boolean {

                if (childView == null) {
                    val recyclerView = view.rootView.findViewById<View>(recyclerViewId) as RecyclerView
                    if (recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView
                    } else {
                        return false
                    }
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    view === targetView
                }
            }
        }
    }
}
