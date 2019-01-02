package homeway.com.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import homeway.com.challenge.animation.Dismissable
import homeway.com.challenge.fragment.VenueSearchFragment
import homeway.com.viewmodel.VenueSharedViewModel
import javax.inject.Inject

/**
 * Venue Activity is the base activity of this application. It will serve as the launcher activity
 * as well as the activity that will house all of the fragments contained within this application.
 */
class VenueActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedViewModel: VenueSharedViewModel

    // We add a dispatching injector to an Activity so that we are able to utilize Dagger Android for
    //activity and fragment injection
    @Inject
    lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment>  = mDispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_activity)

        if( savedInstanceState == null ){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    VenueSearchFragment.newInstance()).commit()
        }

        sharedViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VenueSharedViewModel::class.java)

        setSupportActionBar(findViewById(R.id.toolbar))

        actionBar?.title = getString(R.string.app_name)
    }

    /**
     * We override the native functionality of onBackPressed() to handle the animation of
     * any dismissable pages if applicable
     */
    override fun onBackPressed() {

        val dismissable = topDismissableFragment()

        dismissable?.dismiss(object:Dismissable.OnDismissedListener {
            override fun onDismissed() {
                supportFragmentManager.popBackStackImmediate()
            }
        }) ?: run { super.onBackPressed() }
    }

    /**
     * This is a convenience method that will retrieve the top fragment of the stack and attempt to
     * cast to it as a Dismissable. Dismissable is an interface that will apply exit animation to
     * a fragment
     *
     * @return the top fragment as a dismissable if there are any fragments on the backstack, null otherwise
     */
    private fun topDismissableFragment(): Dismissable? {
        if( supportFragmentManager.backStackEntryCount == 0 ){
            return null
        }

        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount-1).name?.let{
            return supportFragmentManager.findFragmentByTag(it) as? Dismissable
        }

        return null
    }
}
