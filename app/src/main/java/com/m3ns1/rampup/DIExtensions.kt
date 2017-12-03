package com.m3ns1.rampup

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment

/**
 * Just a helper extension that will return the full qualified class
 * name
 */
fun Any.fqn(): String {
    return javaClass.canonicalName
}

/**
 * Get the [AppComponent] for this application with the [AppModule]
 */
fun Application.withComponent(): AppComponent {
    return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
}

/**
 * Get the [ActivityComponent] that belongs to this activity
 */
fun Activity.component(): ActivityComponent {
    return this.application.withComponent().activityComponentBuilder().with(ActivityModule(this)).build()
}

/**
 * Get a new [FragmentComponent] for this [FragmentCallback] resp. from its host activity
 */
fun android.app.Fragment.componentWithin(callback: FragmentCallback?): FragmentComponent? = callback?.hostActivity?.component()?.fragmentComponentBuilder()?.with(FragmentModule(this))?.build()
