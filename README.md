# Dagger2 with Kotlin

This small project will get you going with Dagger 2 and Kotlin for your Android projects so 
you can focus on writing your business logic.

## Enable Kotlin Support
To enable kotlin you can use the `build.gradle` file from this projects root folder. Just 
update the `ext.kotlin_version` variable to the latest version. Also check the documentation 
at [Kotlin](https://kotlinlang.org/docs/reference/using-gradle.html).

## Get dagger2
First you need to add the dagger dependency for your project. Find out the latest version on 
[Dagger Releases](https://github.com/google/dagger/releases) and add the dependency to your module
build.gradle (replace 2.13 if there's a newer version available):

```
dependencies {
    ...
    implementation 'com.google.dagger:dagger:2.13'
    annotationProcessor 'com.google.dagger:dagger-compiler:2.13'
}
```

## Make it play together
In dagger docs you will be told to add this to your modules `build.gradle` file:

```
// Add Dagger dependencies
dependencies {
  compile 'com.google.dagger:dagger:2.x'
  annotationProcessor 'com.google.dagger:dagger-compiler:2.x'
}
```

But when you want to use kotlin with your project you must change the `annotationProcessor`
with the Kotlin annotation processor `kapt`:

```
dependencies {
    compile 'com.google.dagger:dagger:2.x'
    kapt 'com.google.dagger:dagger-compiler:2.13'
    kapt 'com.google.dagger:dagger-android-processor:2.13'
}

```

## Dagger setup
Dagger uses the concept of scopes in order to provide the right instances in a given 
context. I usually have three scopes:
* Global
* Per activity
* Per fragment

For `global` I use the existing `@Singleton` scope. For the other two scopes I define two
custom annotations:

```kotlin
@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class PerActivity


@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class PerFragment

```

Each scope has its own component that declares its module dependencies:

```kotlin
@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent {

    fun activityComponentBuilder(): ActivityComponent.Builder

    fun inject(application: Application)
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
}

@PerFragment
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    @Subcomponent.Builder
    interface Builder {

        fun with(module: FragmentModule): Builder

        fun build(): FragmentComponent
    }

}

```

The application module will provide some defaults such as the application version 
in the form `VERSION_NAME (BUILD_NR)`. The other modules are empty at the moment. This
is where you will later provide your scoped instances.

# Use Kotlin for Dagger DI Setup
Dagger will generate code for our dagger `@Component`s and will use the `@Provides` 
annotated methods to get the right instances for the known scopes. In order to use this DI
feature for our application we need to setup the component that we want to use. Typically this
is done somewhere in our custom Application class.

Kotlin has a nice feature called `Extension Functions`. With those functions you
can add new features to existing classes. So the idea now is to write some extension
functions that will setup dagger correctly and we can use those extension functions through 
out our application (activities/fragments etc.) and we can use this extensions
in any other project (maybe we can use this later as a library in other projects instead
of copy-pasting it along new projects)

## Boiler-plate code with Kotlin
The `DIExtensions.kt` file provides all the code that will setup dagger for your 
project and which should work in any Dagger/Kotlin project without any modifications

```kotlin
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
fun android.app.Fragment.componentWithin(callback: Context?): FragmentComponent? = callback?.component()?.fragmentComponentBuilder()?.with(FragmentModule(this))?.build()
```

## How-to use
Now you can use Dagger DI in your Application, Activities and Fragments with one simple call:

```kotlin
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



class MActivity : Activity() {
    @Inject
    @field:Named(Names.APPLICATION_VERSION)
    lateinit var appVersion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        component().inject(this)
        if (savedInstanceState == null) {
            // load the example fragment inside content
            fragmentManager.beginTransaction()
                    .add(R.id.content, MFragment.newFragment(), MFragment.fqn())
                    .commit()
        }
    }
}

class MFragment : Fragment() {

    companion object {
        fun newFragment(): Fragment {
            return MFragment()
        }
    }

    @Inject
    @field:Named(Names.APPLICATION_VERSION)
    lateinit var appVersion: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        componentWithin(context)?.inject(this)
    }
}

```

That's it