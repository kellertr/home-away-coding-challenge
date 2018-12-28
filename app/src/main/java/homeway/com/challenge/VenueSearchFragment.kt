package homeway.com.challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import homeway.com.challenge.view.VenueAdapter
import homeway.com.challenge.view.VenueRowInterface
import homeway.com.viewmodel.VenueListViewModel
import homeway.com.viewmodel.model.VenueSearchDisplay
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class VenueSearchFragment : Fragment(), VenueRowInterface {
    val TAG = VenueSearchFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var venueListViewModel: VenueListViewModel

    lateinit var venueListAdapter: VenueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        venueListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VenueListViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        venueListAdapter = VenueAdapter(view = venueList, venueRowInterface = this)
        venueList.layoutManager = LinearLayoutManager(context)
        venueList.adapter = venueListAdapter

        venueSearch.requestFocus()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        venueListViewModel.getVenueListLiveData().observe(viewLifecycleOwner, updateVenueList)
        venueListViewModel.getVenueModifiedLiveData().observe(viewLifecycleOwner, updateVenueFavorited)
    }

    private val updateVenueList = Observer<List<VenueSearchDisplay>> { venues ->
        Log.v(TAG, "Venues received")
        venueListAdapter.data = venues.toMutableList()
        venueListAdapter.notifyDataSetChanged()
    }

    private val updateVenueFavorited = Observer<Pair<Int, VenueSearchDisplay>> { venuePosition ->

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

                return true
            }
        } )
    }

    override fun onFavoriteClicked(venueSearchDisplay: VenueSearchDisplay, position: Int) {
        venueListViewModel.venueFavorited(venueSearchDisplay, position)
    }

    override fun onRowClicked(venueSearchDisplay: VenueSearchDisplay) {

    }
}
