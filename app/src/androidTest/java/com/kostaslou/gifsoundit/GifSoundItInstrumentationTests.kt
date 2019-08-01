package com.kostaslou.gifsoundit

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kostaslou.gifsoundit.robots.TopDialogButton
import com.kostaslou.gifsoundit.robots.home
import com.kostaslou.gifsoundit.ui.MainActivity
import com.kostaslou.gifsoundit.util.commons.PostType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GifSoundItInstrumentationTests {

    @get:Rule
    val mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun moreEspressoLessDepresso() {
        home {
            // wait a bit because we may need a token refresh
            sleep(2000)
        }
    }

    @Test
    fun when_at_home_and_we_navigate_from_a_category_to_the_same() {
        home {
            // the default is hot
            more()
            hot()
            textColorsOkWhenPostTypeIs(PostType.HOT)
            filterMenuIsGone()

            // hot is ok, let's try with new also
            more()
            new()
            textColorsOkWhenPostTypeIs(PostType.NEW)
            filterMenuIsGone()
        }
    }

    @Test
    fun when_at_home_and_we_navigate_to_every_category() {
        home {
            // hot to new
            more()
            new()
            textColorsOkWhenPostTypeIs(PostType.NEW)
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)

            // new to top
            more()
            top()
            clickDialogButton(TopDialogButton.ALL)
            textColorsOkWhenPostTypeIs(PostType.TOP)
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)

            // top to hot
            more()
            hot()
            textColorsOkWhenPostTypeIs(PostType.HOT)
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)
        }
    }

    @Test
    fun when_at_home_and_we_navigate_to_opengs() {
        home {
            listHasData()
            openGS {}
        }
    }

    @Test
    fun when_at_opengs_and_we_reduce_the_seconds_offset() {
        home {
            listHasData()
        } openGS {
            getFragmentInstance(mActivityTestRule)
            reduceSecondsAndCheck()
        }
    }

    companion object {
        const val TIME_TO_WAIT_FOR_DATA_FETCH = 1000L
    }
}