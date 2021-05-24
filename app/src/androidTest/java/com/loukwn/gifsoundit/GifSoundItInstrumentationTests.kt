package com.loukwn.gifsoundit

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.loukwn.gifsoundit.postdata.FilterTypeDTO
import com.loukwn.gifsoundit.postdata.TopFilterTypeDTO
import com.loukwn.gifsoundit.robots.TopDialogButton
import com.loukwn.gifsoundit.robots.home
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifSoundItInstrumentationTests {

    @get:Rule
    val mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

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
            textColorsOkWhenPostTypeIs(FilterTypeDTO.Hot)
            new()
            textColorsOkWhenPostTypeIs(FilterTypeDTO.New)
            filterMenuIsGone()
        }
    }

    @Test
    fun when_at_home_and_we_navigate_to_every_category() {
        home {
            // hot to new
            more()
            new()
            textColorsOkWhenPostTypeIs(FilterTypeDTO.New)
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)

            // new to top
            more()
            top()
            clickDialogButton(TopDialogButton.ALL)
            textColorsOkWhenPostTypeIs(FilterTypeDTO.Top(TopFilterTypeDTO.ALL))
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)

            // top to hot
            more()
            hot()
            textColorsOkWhenPostTypeIs(FilterTypeDTO.Hot)
            filterMenuIsGone()

            // sometimes there is a delay during data fetch
            sleep(TIME_TO_WAIT_FOR_DATA_FETCH)
        }
    }

    companion object {
        const val TIME_TO_WAIT_FOR_DATA_FETCH = 1000L
    }
}
