package homeway.com.challenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.viewmodel.model.DisplayVenue
import java.util.*

class VenueAdapter(var data: MutableList<DisplayVenue> = Collections.emptyList(), val view: RecyclerView, val venueRowInterface: VenueRowInterface)
    : RecyclerView.Adapter<VenueViewHolder>(), VenueFavoritedInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VenueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.venue_row,
            parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int)  {
        val venue = data.get(position)
        holder.bind(venue, this)

        holder.itemView.setOnClickListener {
            venueRowInterface.onRowClicked( venue )
        }
    }

    override fun venueFavoriteAdjusted(displayVenue: DisplayVenue, position: Int) {
        venueRowInterface.onFavoriteClicked(displayVenue, position)
    }
}

interface VenueFavoritedInterface {
    fun venueFavoriteAdjusted(displayVenue: DisplayVenue, position: Int)
}