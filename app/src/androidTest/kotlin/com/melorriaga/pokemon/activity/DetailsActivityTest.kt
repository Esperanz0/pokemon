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
import com.melorriaga.pokemon.view.impl.DetailsActivity
import io.appflate.restmock.RESTMockServer.whenGET
import io.appflate.restmock.RequestsVerifier.verifyGET
import io.appflate.restmock.utils.RequestMatchers.pathEndsWith
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by melorriaga on 4/2/17.
 */
class DetailsActivityTest : BaseActivityTest() {

    @Rule
    @JvmField
    var detailsActivityTestRule = ActivityTestRule(
            DetailsActivity::class.java,    // activityClass
            true,                           // initialTouchMode
            false                           // launchActivity
    )

    @Test
    fun testShowPokemonDetails_success() {
        whenGET(pathEndsWith("pokemon/25/"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnFile(200, "getPokemonDetails_200.json")

        val intent = Intent()
        intent.putExtra("pokemonId", 25)
        intent.putExtra("pokemonName", "pikachu")
        detailsActivityTestRule.launchActivity(intent)

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pokemon_details_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.pokemon_id)).check(matches(withText("id: 25")))
        onView(withId(R.id.pokemon_name)).check(matches(withText("name: pikachu")))
        onView(withText(R.string.done)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon/25/")).invoked()
    }

    @Test
    fun testShowPokemonDetails_error() {
        whenGET(pathEndsWith("pokemon/25/"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnEmpty(404)

        val intent = Intent()
        intent.putExtra("pokemonId", 25)
        intent.putExtra("pokemonName", "pikachu")
        detailsActivityTestRule.launchActivity(intent)

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.pokemon_details_layout)).check(matches(isDisplayed()))
        onView(withText(R.string.error)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon/25/")).invoked()
    }

    @Test
    fun testShowPokemonDetails_error_retry() {
        whenGET(pathEndsWith("pokemon/25/"))
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnEmpty(404)
                .delay(TimeUnit.SECONDS, 1)
                .thenReturnFile(200, "getPokemonDetails_200.json")

        val intent = Intent()
        intent.putExtra("pokemonId", 25)
        intent.putExtra("pokemonName", "pikachu")
        detailsActivityTestRule.launchActivity(intent)

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withText(R.string.error)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withText(R.string.retry)).perform(click())
        onView(withId(R.id.pokemon_details_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.pokemon_id)).check(matches(withText("id: 25")))
        onView(withId(R.id.pokemon_name)).check(matches(withText("name: pikachu")))
        onView(withText(R.string.done)).check(matches(withEffectiveVisibility(VISIBLE)))

        verifyGET(pathEndsWith("pokemon/25/")).exactly(2)
    }

}
