package com.m3ns1.rampup

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.content.Context

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
fun Context.withComponent(): AppComponent {
    return DaggerAppComponent.builder()
            .appModule(AppModule(this as Application))
            .build()
}

/**
 * Get the [ActivityComponent] that belongs to this activity
 */
fun Context.component(): ActivityComponent {
    if (this is Activity) {
        return this.application.withComponent().activityComponentBuilder().with(ActivityModule(this)).build()
    } else {
        throw IllegalArgumentException("You must call this from an activity")
    }
}

/**
 * Get a new [FragmentComponent] for this [FragmentCallback] resp. from its host activity
 */
fun Fragment.componentWithin(callback: Context?): FragmentComponent? = callback?.component()?.fragmentComponentBuilder()?.with(FragmentModule(this))?.build()
