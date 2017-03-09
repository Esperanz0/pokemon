package com.melorriaga.pokemon.assertion

import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.v7.widget.RecyclerView
import android.view.View

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

/**
 * Created by melorriaga on 3/2/17.
 */
class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter.itemCount, `is`(expectedCount))
    }

}
