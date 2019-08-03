package com.kostaslou.gifsoundit.robots

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.MainActivity
import com.kostaslou.gifsoundit.ui.open.OpenGSFragment
import kotlinx.android.synthetic.main.fragment_opengs.*

class OpenGSRobot : BaseRobot() {

    private lateinit var frag: OpenGSFragment

    // fragment based
    fun getFragmentInstance(mActivityScenarioRule: ActivityScenarioRule<MainActivity>) {
        mActivityScenarioRule.scenario.onActivity {
            frag = it.supportFragmentManager.findFragmentById(R.id.fragContainer) as OpenGSFragment
        }
    }

    private fun getOffsetAsNumber(): Int =
        frag.offsetLabel.text.split(" ")[2].toInt()

    fun reduceSecondsAndCheck() {
        // get current offset
        val oldSeconds = getOffsetAsNumber()

        // decrease
        clickButton(R.id.decreaseButton)

        // if the offset is zero, it should not decrease any more
        if (oldSeconds > 0) {
            matchText(R.id.offsetLabel, frag.getString(R.string.opengs_label_offset, oldSeconds - 1))
        } else
            matchText(R.id.offsetLabel, frag.getString(R.string.opengs_label_offset, oldSeconds))
    }
}
