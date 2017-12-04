package com.m3ns1.rampup.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.m3ns1.rampup.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by m3ns1 on 03.12.17.
 */
class MActivity : Activity() {
    @Inject
    @field:Named(Names.APPLICATION_VERSION)
    lateinit var appVersion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        component().inject(this)

        Log.d(fqn(), "Activity startup: $appVersion")
        if (savedInstanceState == null) {
            // load the example fragment inside content
            fragmentManager.beginTransaction()
                    .add(R.id.content, MFragment.newFragment(), MFragment.fqn())
                    .commit()
        }
    }
}