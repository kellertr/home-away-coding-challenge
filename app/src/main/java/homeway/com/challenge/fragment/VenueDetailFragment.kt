package homeway.com.challenge.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import homeway.com.challenge.R
import homeway.com.viewmodel.VenueDetailViewModel
import homeway.com.viewmodel.VenueSharedViewModel
import javax.inject.Inject



class VenueDetailFragment : Fragment() {

    val TAG = VenueDetailFragment::class.java.simpleName

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var venueDetailViewModel: VenueDetailViewModel
    private lateinit var sharedVenueViewModel: VenueSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        venueDetailViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VenueDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.venue_detail_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            sharedVenueViewModel = ViewModelProviders.of(it, viewModelFactory).get(VenueSharedViewModel::class.java)

            sharedVenueViewModel.selectedVenue.value?.let { venue ->
                venueDetailViewModel.venue = venue
            }

            it.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar).title = venueDetailViewModel.venue?.name

            val mapsView = it.findViewById<ImageView>(R.id.mapsDetailImage)
            mapsView.visibility = View.VISIBLE

            val displayMetrics = DisplayMetrics()
            it.windowManager.defaultDisplay.getMetrics(displayMetrics)

            Picasso.get().load(venueDetailViewModel.getGoogleMapsUrl( it.getString(R.string.maps_api_key),
                    displayMetrics.widthPixels,
                    it.resources.getDimensionPixelSize(R.dimen.collapsing_toolbar_height))).into(mapsView)
        }
    }

    override fun onStop() {
        super.onStop()

        activity?.let {

            it.findViewById<ImageView>(R.id.mapsDetailImage).apply {
                setImageDrawable(null)
                visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance(): VenueDetailFragment = VenueDetailFragment()
    }
}