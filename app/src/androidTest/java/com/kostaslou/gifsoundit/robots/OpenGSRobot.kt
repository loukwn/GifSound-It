package com.kostaslou.gifsoundit.robots

import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.kostaslou.gifsoundit.MainActivity
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.opengs.OpenGSFragment

class OpenGSRobot(mActivityScenarioRule: ActivityScenarioRule<MainActivity>) : BaseRobot() {

    private var frag: OpenGSFragment? = null

    init {
        mActivityScenarioRule.scenario.onActivity {
            it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.let { navHost ->
                frag = navHost.childFragmentManager.fragments[0] as OpenGSFragment
            }
        }
    }

    private fun getOffsetAsNumber(openFrag: OpenGSFragment): Int {
        openFrag.view?.findViewById<TextView>(R.id.offsetLabel)?.let {
            return it.text.split(" ")[2].toInt()
        } ?: return -1
    }


    fun reduceSecondsAndCheck() {
        frag?.let {
            // get current offset
            val oldSeconds = getOffsetAsNumber(it)

            // decrease
            clickButton(R.id.decreaseButton)

            // if the offset is zero, it should not decrease any more
            if (oldSeconds > 0) {
                matchText(R.id.offsetLabel, it.getString(R.string.opengs_label_offset, oldSeconds - 1))
            } else
                matchText(R.id.offsetLabel, it.getString(R.string.opengs_label_offset, oldSeconds))
        }
    }
}
