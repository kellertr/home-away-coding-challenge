package homeway.com.challenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.viewmodel.model.DisplayVenue
import java.util.*

/**
 * The VenueAdapter class is a RecyclerView Adapter that will bind DisplayVenues to a recycler View.
 * This class depends on VenueViewHolder for binding views in the recycler view. This class also
 * utilizes a VenueRowInterface that is utilized to inform a calling class that a venue has been
 * marked as a favorite. The Venue Row Interface also informs an implementing class that a row has
 * been clicked and to handle this action
 */
class VenueAdapter(var data: MutableList<DisplayVenue> = Collections.emptyList(), val view: RecyclerView, val venueRowInterface: VenueRowInterface)
    : RecyclerView.Adapter<VenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VenueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.venue_row,
            parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int)  {
        val venue = data.get(position)
        holder.bind(venue, venueRowInterface)

        holder.itemView.setOnClickListener {
            venueRowInterface.onRowClicked( venue )
        }
    }
}