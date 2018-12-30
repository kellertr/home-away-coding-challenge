package homeway.com.challenge.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import homeway.com.challenge.R
import homeway.com.viewmodel.VenueDetailViewModel
import homeway.com.viewmodel.VenueSharedViewModel
import javax.inject.Inject

class VenueDetailFragment : Fragment() {

    val TAG = VenueDetailFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var venueListViewModel: VenueDetailViewModel
    private lateinit var sharedVenueViewModel: VenueSharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.venue_detail_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            sharedVenueViewModel = ViewModelProviders.of(it, viewModelFactory).get(VenueSharedViewModel::class.java)
        }
    }
}