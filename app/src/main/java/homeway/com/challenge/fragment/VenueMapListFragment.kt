package homeway.com.challenge.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import homeway.com.challenge.animation.FabAnimationUtils
import homeway.com.challenge.animation.Dismissable
import homeway.com.challenge.animation.RevealAnimationSetting
import homeway.com.viewmodel.VenueSharedViewModel
import javax.inject.Inject
import com.google.android.gms.maps.CameraUpdateFactory

/**
 * The VenueMapListFragment displays a list of venues on a google map. It displays an item with a pin
 * at each venue position and will navigate to the VenueDetailFragment upon clicking any of the pins.
 */
class VenueMapListFragment : SupportMapFragment(), OnMapReadyCallback, Dismissable {

    private var mMap: GoogleMap? = null
    private lateinit var fabAnimationSettings: RevealAnimationSetting

    private var sharedVenueViewModel: VenueSharedViewModel? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val mapView = super.onCreateView(inflater, container, savedInstanceState)!!

        //Retrieve our animation settings from the bundle that we will utilize to animate out of this fragment
        arguments?.getParcelable<RevealAnimationSetting>(ANIMATION_SETTINGS_TAG)?.let { animationSettings ->
            fabAnimationSettings = animationSettings
            FabAnimationUtils.registerCircularRevealAnimation(mapView, animationSettings)
        }

        return mapView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            sharedVenueViewModel = ViewModelProviders.of(it, viewModelFactory).get(VenueSharedViewModel::class.java)

            if( mMap != null ){
                displayVenueMarkers()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setMinZoomPreference(10f)

        if (sharedVenueViewModel != null) {
            displayVenueMarkers()
        }

        //We add an onInfoWindow click listener to process clicks on each individual marker window. Upon clicking on the
        //info window, we iterate through the venues in the shared view model and set the selected
        //venue in the shared view model here
        mMap?.setOnInfoWindowClickListener { marker ->
            sharedVenueViewModel?.venues?.value?.let { venues ->
                sharedVenueViewModel?.selectedVenue?.value = venues.find {
                    it.name == marker.title && it.latitude == marker.position.latitude && it.longitude == marker.position.longitude }

                FragmentRunner.activateNewFragment( activity, VenueDetailFragment.newInstance() )
            }
        }
    }

    /**
     * displayVenueMarkers() is responsible for reading menus from the shared view model and displaying
     * the markers on the Google Map
     */
    private fun displayVenueMarkers() {
        mMap?.clear()

        val boundBuilder = LatLngBounds.builder()

        sharedVenueViewModel?.venues?.value?.let { venues ->
            for (venue in venues) {
                val venueLocation = LatLng(venue.latitude, venue.longitude)
                boundBuilder.include(venueLocation)

                val marker = MarkerOptions().position(venueLocation).title(venue.name)

                mMap?.addMarker(marker)
            }
        }

        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 20))
    }

    override fun dismiss(listener: Dismissable.OnDismissedListener) {
        context?.let { _ ->
            view?.let { view ->
                FabAnimationUtils.startCircularExitAnimation(view, fabAnimationSettings, listener)
            }
        }
    }

    companion object {

        private const val ANIMATION_SETTINGS_TAG = "ANIMATION_SETTINGS"

        /**
         * Create a new instance of the VenueMapListFragment
         *
         * @param setting  the reveal animation settings we use when exiting the VenueMapListFragment
         * @return a new instance of the VenueMapListFragment
         */
        fun newInstance(setting: RevealAnimationSetting): VenueMapListFragment {
            val frag = VenueMapListFragment()
            val bundle = Bundle().apply {
                putParcelable(ANIMATION_SETTINGS_TAG, setting)
            }

            frag.arguments = bundle
            return frag
        }
    }
}
