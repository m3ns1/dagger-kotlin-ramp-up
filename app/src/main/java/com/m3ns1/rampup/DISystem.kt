package com.m3ns1.rampup

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.content.Context
import com.m3ns1.rampup.ui.MActivity
import com.m3ns1.rampup.ui.MFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Scope
import javax.inject.Singleton

/**
 * Created by m3ns1 on 03.12.17.
 */
@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent {

    fun activityComponentBuilder(): ActivityComponent.Builder

    // YOUR INJECTABLE APPLICATION GOES HERE
    fun inject(application: MApplication)
}

@Subcomponent(modules = arrayOf(ActivityModule::class))
@PerActivity
interface ActivityComponent {

    @Subcomponent.Builder
    interface Builder {

        fun with(module: ActivityModule): Builder

        fun build(): ActivityComponent

    }

    fun fragmentComponentBuilder(): FragmentComponent.Builder

    // YOUR INJECTABLE ACTIVITIES GO HERE...

    fun inject(mActivity: MActivity)
}

@PerFragment
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    @Subcomponent.Builder
    interface Builder {

        fun with(module: FragmentModule): Builder

        fun build(): FragmentComponent
    }

    // YOUR INJECTABLE FRAGMENTS GO HERE...

    fun inject(mFragment: MFragment)

}

// use android.app.Application instead of your custom Application!!!
@Module
@Singleton
class AppModule(val application: Application) {

    @Provides
    fun provideApplication(): Application {
        return application
    }

    @Provides
    @Named(Names.APPLICATION_CONTEXT)
    fun provideApplicationContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Named(Names.APPLICATION_VERSION)
    fun provideVersion(): String {
        return "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}

@Module
@PerActivity
class ActivityModule(val ui: Activity) {

}

@Module
@PerFragment
class FragmentModule(val view: Fragment) {

}

/**
 * A callback for fragments
 */
interface FragmentCallback {
    val hostActivity: Activity
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class PerActivity


@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class PerFragment
