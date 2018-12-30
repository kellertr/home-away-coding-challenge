package homeway.com.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import homeway.com.challenge.animation.Dismissable
import homeway.com.challenge.fragment.VenueSearchFragment
import homeway.com.viewmodel.VenueSharedViewModel
import kotlinx.android.synthetic.main.venue_activity.*
import javax.inject.Inject

class VenueActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedViewModel: VenueSharedViewModel

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

    @Inject
    lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return mDispatchingAndroidInjector
    }

    override fun onBackPressed() {

        val dismissable = topDismissableFragment()

        if(dismissable != null){
            dismissable.dismiss(object:Dismissable.OnDismissedListener {
                override fun onDismissed() {
                    supportFragmentManager.popBackStackImmediate()
                }
            })
        } else {
            super.onBackPressed()
        }
    }

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
