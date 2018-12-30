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
import homeway.com.challenge.R


class VenueMapListFragment : SupportMapFragment(), OnMapReadyCallback, Dismissable {
    private val TAG = VenueMapListFragment::class.java.simpleName

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

        context?.let { context ->
            arguments?.getParcelable<RevealAnimationSetting>(ANIMATION_SETTINGS_TAG)?.let { animationSettings ->
                fabAnimationSettings = animationSettings
                FabAnimationUtils.registerCircularRevealAnimation(context, mapView, animationSettings)
            }
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

        if (sharedVenueViewModel != null) {
            displayVenueMarkers()
        }

        mMap?.setOnInfoWindowClickListener { marker ->
            sharedVenueViewModel?.venues?.value?.let { venues ->
                sharedVenueViewModel?.selectedVenue?.value = venues.find {
                    it.name == marker.title && it.latitude == marker.position.latitude && it.longitude == marker.position.longitude }

                val fragmentTag = VenueDetailFragment::class.java.simpleName
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, VenueDetailFragment.newInstance(),
                                fragmentTag)?.addToBackStack(fragmentTag)?.commit()
            }
        }
    }

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
        context?.let { context ->
            view?.let { view ->
                FabAnimationUtils.startCircularExitAnimation(context, view, fabAnimationSettings, listener)
            }
        }
    }

    companion object {

        private const val ANIMATION_SETTINGS_TAG = "ANIMATION_SETTINGS"

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
