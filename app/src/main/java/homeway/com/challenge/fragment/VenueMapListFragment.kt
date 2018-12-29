package homeway.com.challenge.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import homeway.com.challenge.animation.FabAnimationUtils
import homeway.com.challenge.animation.Dismissable
import homeway.com.challenge.animation.RevealAnimationSetting


class VenueMapListFragment : SupportMapFragment(), OnMapReadyCallback, Dismissable {
    private val TAG = VenueMapListFragment::class.java.simpleName

    private lateinit var mMap: GoogleMap
    private lateinit var fabAnimationSettings: RevealAnimationSetting

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    override fun dismiss(listener: Dismissable.OnDismissedListener) {
        context?.let{ context ->
            view?.let{ view ->
                FabAnimationUtils.startCircularExitAnimation(context, view, fabAnimationSettings, listener)
            }
        }
    }

    companion object {

        private const val ANIMATION_SETTINGS_TAG = "ANIMATION_SETTINGS"

        fun newInstance( setting: RevealAnimationSetting ): VenueMapListFragment{
            val frag = VenueMapListFragment()
            val bundle = Bundle().apply {
                putParcelable(ANIMATION_SETTINGS_TAG, setting)
            }

            frag.arguments = bundle
            return frag
        }
    }
}
