package homeway.com.challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import homeway.com.viewmodel.VenueListViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject



/**
 * A placeholder fragment containing a simple view.
 */
class VenueSearchFragment : Fragment() {
    val TAG = VenueSearchFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var venueListViewModel: VenueListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        venueListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VenueListViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewLifecycleOwner.apply {
            venueListViewModel.getVenueListLiveData().observe(this, updateVenueList)
        }
    }

    val updateVenueList = Observer<List<String>> { venues ->
        Log.v("", "Venues received $venues")
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
}
