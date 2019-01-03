package homeway.com.challenge.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import homeway.com.challenge.R
import homeway.com.viewmodel.VenueDetailViewModel
import homeway.com.viewmodel.VenueSharedViewModel
import homeway.com.viewmodel.model.DisplayVenue
import kotlinx.android.synthetic.main.venue_detail_fragment.*
import javax.inject.Inject
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.google.android.material.snackbar.Snackbar

/**
 * The VenueDetailFragment will show more information about a Venue. Upon loading this page, we get
 * the selected venue from the shared view model and then load any more relevant information about
 * this venue.
 */
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
        }

        venueDetailViewModel.venueLiveData.observe(viewLifecycleOwner, updateVenues)
    }

    override fun onResume() {
        super.onResume()

        activity?.let {
            val mapsView = it.findViewById<ImageView>(R.id.mapsDetailImage)
            mapsView.visibility = View.VISIBLE

            //We need to get the width of the screen so we load the correct size map image
            val displayMetrics = DisplayMetrics()
            it.windowManager.defaultDisplay.getMetrics(displayMetrics)

            Picasso.get().load(venueDetailViewModel.getGoogleMapsUrl( it.getString(R.string.maps_api_key),
                    displayMetrics.widthPixels,
                    it.resources.getDimensionPixelSize(R.dimen.collapsing_toolbar_height))).into(mapsView)

            //Enable the collapsing toolbar layout and alter the functionality from a regular toolbar
            it.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar).apply {
                isTitleEnabled = true
                title = venueDetailViewModel.venueLiveData.value?.name
            }
        }
    }

    override fun onPause() {
        super.onPause()

        activity?.let {
            it.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar).isTitleEnabled = false
        }
    }

    private val updateVenues = Observer<DisplayVenue> { venue->
        Log.v(TAG, "Venue received $venue")

        venueFavorited.visibility = if(venue.favorite) View.VISIBLE else View.GONE

        //The detail call only provides us a venue outside of what we already show from the venue row
        venue.url?.let {url ->
            venueDetailLink.visibility = View.VISIBLE
            venueDetailLink.setOnClickListener {
                try {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    view?.let { view->
                        Snackbar.make( view, R.string.unable_to_open_link, Snackbar.LENGTH_LONG ).show()
                    }
                }

            }
        } ?: run { venueDetailLink.visibility = View.GONE }
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

        /**
         * @return a new instance of the venue detail fragment
         */
        fun newInstance(): VenueDetailFragment = VenueDetailFragment()
    }
}