package com.better.alarm.test

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import android.view.View
import com.better.alarm.R
import com.better.alarm.configuration.AlarmApplication
import com.better.alarm.presenter.AlarmsListActivity
import com.better.alarm.util.Optional
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class PickerAmTest : BaseTest() {
    @JvmField
    var listActivity = ActivityTestRule(
            AlarmsListActivity::class.java, false, /* autostart*/ true)

    @JvmField
    @Rule
    var chain: TestRule = RuleChain.outerRule(ForceLocaleRule(Locale.US)).around(listActivity)

    class OnView {
        fun with(id: Int? = null, text: String? = null): ViewInteraction {
            val matcher = listOfNotNull(
                    text?.let { ViewMatchers.withText(it) },
                    id?.let { ViewMatchers.withId(it) }
            ).let { Matchers.allOf(it) }
            return Espresso.onView(matcher)
        }
    }

    fun onView(): OnView {
        return OnView()
    }

    fun ViewInteraction.click() {
        perform(ViewActions.click())
    }

    fun ViewInteraction.assertDisabled() {
        check(matches(not<View>(isEnabled())))
    }

    fun ViewInteraction.assertEnabled() {
        check(matches(isEnabled()))
    }

    @Before
    fun setUp() {
        AlarmApplication.is24hoursFormatOverride = Optional.of(false)
        onView().with(id = R.id.fab).click()
        //sleep();
    }

    @After
    override fun tearDown() {
        onView().with(text = "Cancel").click()
        //sleep();
        onView().with(text = "Cancel").click()
        assertThatList().items().hasSize(2)
    }

    @Test
    fun testNothing() {
        assertTimerView("--:--")
        arrayOf(one(), two(), three(), four(), five(), six(), seven(), eight(), nine()).forEach { it.assertEnabled() }
        zero().assertDisabled()
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test1() {
        one().click()
        arrayOf(zero(), one(), two()).forEach { it.assertEnabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test2() {
        two().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test3() {
        three().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test4() {
        four().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test5() {
        five().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test6() {
        six().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test7() {
        seven().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test8() {
        eight().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test9() {
        nine().click()
        arrayOf(zero(), one(), two(), three(), four(), five()).forEach { it.assertEnabled() }
        arrayOf(six(), seven(), eight(), nine()).forEach { it.assertDisabled() }
        ok().assertDisabled()
        pm().assertDisabled()
    }

    @Test
    fun test1230pm() {
        assert(one() == one())

        one().click()
        two().click()
        three().click()
        zero().click()
        pm().click()

        arrayOf<ViewInteraction>().forEach { it.assertEnabled() }
        pm().assertDisabled()
        ok().assertEnabled()
    }

    private fun one(): ViewInteraction = onView().with(id = R.id.key_left, text = "1")
    private fun two(): ViewInteraction = onView().with(id = R.id.key_middle, text = "2")
    private fun three(): ViewInteraction = onView().with(id = R.id.key_right, text = "3")
    private fun four(): ViewInteraction = onView().with(id = R.id.key_left, text = "4")
    private fun five(): ViewInteraction = onView().with(id = R.id.key_middle, text = "5")
    private fun six(): ViewInteraction = onView().with(id = R.id.key_right, text = "6")
    private fun seven(): ViewInteraction = onView().with(id = R.id.key_left, text = "7")
    private fun eight(): ViewInteraction = onView().with(id = R.id.key_middle, text = "8")
    private fun nine(): ViewInteraction = onView().with(id = R.id.key_right, text = "9")
    private fun zero(): ViewInteraction = onView().with(id = R.id.key_middle, text = "0")
    private fun ok(): ViewInteraction = onView().with(text = "OK")
    private fun am(): ViewInteraction = onView().with(id = R.id.key_left, text = "AM")
    private fun pm(): ViewInteraction = onView().with(id = R.id.key_right, text = "PM")
}