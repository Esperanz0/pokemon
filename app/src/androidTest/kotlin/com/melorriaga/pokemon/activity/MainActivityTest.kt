package com.melorriaga.pokemon.activity

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import android.support.test.rule.ActivityTestRule
import com.melorriaga.pokemon.BaseActivityTest
import com.melorriaga.pokemon.R
import com.melorriaga.pokemon.assertion.RecyclerViewItemCountAssertion
import com.melorriaga.pokemon.view.impl.MainActivity
import io.appflate.restmock.RESTMockServer.whenGET
import io.appflate.restmock.RequestsVerifier.verifyGET
import io.appflate.restmock.utils.RequestMatchers.pathEndsWith
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by melorriaga on 3/2/17.
 */
class MainActivityTest : BaseActivityTest() {

    @Rule
    @JvmField
    var mainActivityTestRule = ActivityTestRule(
            MainActivity::class.java,   // activityClass
            true,                       // initialTouchMode
            false                       // launchActivity
    )

    @Test
    fun testShowPokemonNames_success() {
        whenGET(pathEndsWith("pokemon?limit=150"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnFile(200, "getPokemonNames_200.json")

        val intent = Intent()
        mainActivityTestRule.launchActivity(intent)

        onView(withId(R.id.recycler_view)).check(RecyclerViewItemCountAssertion(150))
        onView(withText(R.string.done)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon?limit=150")).invoked()
    }

    @Test
    fun testShowPokemonNames_error() {
        whenGET(pathEndsWith("pokemon?limit=150"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnEmpty(404)

        val intent = Intent()
        mainActivityTestRule.launchActivity(intent)

        onView(withId(R.id.recycler_view)).check(RecyclerViewItemCountAssertion(0))
        onView(withText(R.string.error)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon?limit=150")).invoked()
    }

    @Test
    fun testShowPokemonNames_error_retry() {
        whenGET(pathEndsWith("pokemon?limit=150"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnEmpty(404)
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnFile(200, "getPokemonNames_200.json")

        val intent = Intent()
        mainActivityTestRule.launchActivity(intent)

        onView(withId(R.id.recycler_view)).check(RecyclerViewItemCountAssertion(0))
        onView(withText(R.string.error)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withText(R.string.retry)).perform(click())
        onView(withId(R.id.recycler_view)).check(RecyclerViewItemCountAssertion(150))
        onView(withText(R.string.done)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon?limit=150")).exactly(2)
    }

}
