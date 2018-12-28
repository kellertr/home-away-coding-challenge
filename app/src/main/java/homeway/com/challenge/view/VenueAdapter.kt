package homeway.com.challenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.viewmodel.model.VenueSearchDisplay
import java.util.*

class VenueAdapter(var data: MutableList<VenueSearchDisplay> = Collections.emptyList(), val view: RecyclerView, val venueRowInterface: VenueRowInterface )
    : RecyclerView.Adapter<VenueViewHolder>(), VenueFavoritedInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = VenueViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.venue_row,
            parent, false) )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int)
            = holder.bind(data.get(position), this)

    override fun venueFavoriteAdjusted(venueSearchDisplay: VenueSearchDisplay, position: Int) {
        venueRowInterface.onFavoriteClicked(venueSearchDisplay, position)
    }
}

interface VenueFavoritedInterface {
    fun venueFavoriteAdjusted(venueSearchDisplay: VenueSearchDisplay, position: Int )
}