package com.m3ns1.rampup

import android.app.Application
import android.util.Log
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by m3ns1 on 03.12.17.
 */
class MApplication : Application() {

    @Inject
    @field:Named(Names.APPLICATION_VERSION)
    lateinit var appVersion: String

    override fun onCreate() {
        super.onCreate()
        withComponent().inject(this)

        Log.d(fqn(), "Application started: $appVersion")
    }
}