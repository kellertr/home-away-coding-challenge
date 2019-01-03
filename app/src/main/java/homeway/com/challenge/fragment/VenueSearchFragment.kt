package homeway.com.challenge.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import homeway.com.challenge.R
import homeway.com.challenge.animation.RevealAnimationSetting
import homeway.com.challenge.view.VenueAdapter
import homeway.com.challenge.view.VenueRowInterface
import homeway.com.viewmodel.VenueListViewModel
import homeway.com.viewmodel.VenueSharedViewModel
import homeway.com.viewmodel.model.DisplayVenue
import kotlinx.android.synthetic.main.venue_search_fragment.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class VenueSearchFragment : Fragment(), VenueRowInterface {
    val TAG = VenueSearchFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var venueListViewModel: VenueListViewModel
    private lateinit var sharedVenueViewModel: VenueSharedViewModel
    private lateinit var venueListAdapter: VenueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        venueListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VenueListViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.venue_search_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        venueListAdapter = VenueAdapter(view = venueList, venueRowInterface = this)
        venueList.layoutManager = LinearLayoutManager(context)
        venueList.adapter = venueListAdapter

        venueSearch.requestFocus()

        fab.setOnClickListener {

            if( venueSearch.hasFocus() ){
                hideKeyboard(venueSearch)

                Handler().postDelayed( {
                    navigateToVenueMapFragment()
                }, KEYBOARD_DISMISS_DELAY)
            } else {
                navigateToVenueMapFragment()
            }
        }
    }

    private fun navigateToVenueMapFragment(){
        val fabSettings = RevealAnimationSetting(
                (fab.x + fab.width / 2).toInt(),
                (fab.y + fab.height / 2).toInt(),
                containerView.width,
                containerView.height)

        val venueMapFragment = VenueMapListFragment.newInstance(fabSettings)

        FragmentRunner.activateNewFragment( activity, venueMapFragment )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            sharedVenueViewModel = ViewModelProviders.of(it, viewModelFactory).get(VenueSharedViewModel::class.java)
        }

        venueListViewModel.getVenueListLiveData().observe(viewLifecycleOwner, updateVenueList)
        venueListViewModel.getVenueModifiedLiveData().observe(viewLifecycleOwner, updateVenueFavorited)
    }

    private val updateVenueList = Observer<List<DisplayVenue>> { venues ->
        Log.v(TAG, "Venues received")
        venueListAdapter.data = venues.toMutableList()
        venueListAdapter.notifyDataSetChanged()

        if (venues.isNotEmpty()) { fab.show() } else { fab.hide() }

        sharedVenueViewModel.venues.value = venues
    }

    private val updateVenueFavorited = Observer<Pair<Int, DisplayVenue>> { venuePosition ->

        if( venuePosition != null && venueListAdapter.data.size > venuePosition.first){
            venueListAdapter.data[venuePosition.first] = venuePosition.second
        }
    }

    override fun onResume() {
        super.onResume()

        venueSearch.setOnQueryTextListener( object:SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    venueListViewModel.venueSearchTermUpdated(it)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let{
                    venueListViewModel.venueSearchTermUpdated(it)
                }

                hideKeyboard(venueSearch)

                return true
            }
        } )
    }

    private fun hideKeyboard(focusedView: View){

        if( focusedView.isFocused ){
            return
        }

        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(focusedView.windowToken, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error caught hiding keyboard", e)
        }

        // Dismissing the Keyboard doesn't clear focus on all cases.
        if (focusedView.hasFocus()) {
            focusedView.clearFocus()
        }
    }

    override fun venueFavoriteAdjusted(displayVenue: DisplayVenue, position: Int) {
        venueListViewModel.venueFavorited(displayVenue, position)
    }

    override fun onRowClicked(displayVenue: DisplayVenue) {

        hideKeyboard(venueSearch)

        sharedVenueViewModel.selectedVenue.value = displayVenue

        FragmentRunner.activateNewFragment( activity, VenueDetailFragment.newInstance() )
    }

    companion object {

        private const val KEYBOARD_DISMISS_DELAY: Long = 200

        fun newInstance() : VenueSearchFragment = VenueSearchFragment()
    }
}
